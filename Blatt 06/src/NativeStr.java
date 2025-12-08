import java.util.List;

public class NativeStr extends Fun{
    public NativeStr(Node t, Enviroment env) {
        super(t, env);
    }

    @Override
    public void call(Interpreter ip, List<Object> args) {
        StringBuilder sb = new StringBuilder();
        String s;
        for (int i = 0; i < args.size(); i++) {
            s = args.get(i).toString();
            sb.append(s);
        }
    }
}
