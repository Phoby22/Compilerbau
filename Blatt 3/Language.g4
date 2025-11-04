grammar Language;

// Parser
prog : stmt+ NEWLINE* EOF ;

//Anweisungen
stmt
    : expr NEWLINE          #ExprStmt
    | COMMENT NEWLINE       #CommentStmt
    | cont                  #ContStmt
    | NEWLINE               #NLStmt    // leere Zeilen erlaubt
    ;

// Ausdrücke
expr : e1=expr op='*' e2=expr       # MUL
     | e1=expr op='/' e2=expr       # DIV
     | e1=expr op='+' e2=expr       # ADD
     | e1=expr op='-' e2=expr       # SUB
     | BEZEICHNER                   # VAR
     | STRING                       # STRING
     | NUM                          # ZAHL
     | assign                       # ASSI
     ;

// Variablen
assign : bz=BEZEICHNER ':=' e=expr ;

// Bedingungen
con : e1=expr vp e2=expr ;

// Vergleichsoperatoren
vp : '==' | '!=' | '<' | '>' ;

// Kontrollstrukturen
cont
    : while         #WhileStmt
    | if            #IfStmt
    ;

while : 'while' c=con nw=NEWLINE? 'do' NEWLINE doBlock+=stmt+ 'end' ;
if    : 'if' c=con nw=NEWLINE? 'do' NEWLINE doBlock+=stmt+ ('else do' elseBlock+=stmt+)? 'end' ;

// Lexer
BEZEICHNER : [a-zA-Z_][a-zA-Z0-9_]* ;
NUM : [0-9]+ ;
STRING : '"' (~["\r\n])* '"' ;

NEWLINE : '\r'? '\n' ;
WS : [ \t]+ -> skip ;

COMMENT : '#' ~[\r\n]* -> skip ;
