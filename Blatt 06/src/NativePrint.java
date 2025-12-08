import java.util.List;

public class NativePrint extends Fun {

    public NativePrint(Node t, Enviroment env) {
        super(t, env);
    }

    public void call(Interpreter ip, List<Object> args) {
        for (int i = 0; i < args.size(); i++) {
            System.out.println(args.get(i));
        }
    }
}
