grammar Lisp;

//Parser
program : s_expr+ ;

s_expr : list_expr
       | atom
       ;

list_expr : '(' operator s_expr* ')'
          | '(' 'if' s_expr if_expr ')'
          | '(' def_expr ')'
          | '(' defn_expr ')'
          | '(' let_expr ')'
          | '(' defn_call_expr ')'
          ;

if_expr : s_expr (s_expr)?
        | 'do' s_expr+
        ;

def_expr : 'def' ID s_expr ;

defn_expr : 'defn' ID '(' ID* ')' s_expr ;

defn_call_expr : ID s_expr+ ;

let_expr : 'let' ( '(' ID s_expr ')' ) s_expr+ ;

atom : STRING
     | NUMBER
     | ID
     | boolean
     ;

operator : aop | vop | 'print' | 'str' | 'list' | 'nth' | 'head' | 'tail';

aop : '*' | '/' | '+' | '-' ;

vop : '=' | '<' | '>' ;

boolean : 'true' | 'false' ;

//Lexer

STRING : '"' (~[\n\r"])* '"' ;
NUMBER : [0-9]+ ;
ID : [a-zA-Z][a-zA-Z0-9_]* ;
WS : [ \t\r\n]+ -> skip ;