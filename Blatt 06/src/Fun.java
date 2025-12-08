import java.util.List;

public class Fun implements Callable{
    Node t;
    Enviroment closure;

    public Fun(Node t, Enviroment env) {
        this.t = t;
        closure = env;
    }

    @Override
    public void call(Interpreter ip, List<Object> args) {
        Enviroment prev = ip.env;
        ip.env = new Enviroment(closure);

        for (int i = 0; i < args.size(); i++) {
            //Fun fn_dec = (Fun)this.env.values.get(t.children.get(0).value);
            ip.env.define(t.children.get(i+1).value, args.get(i));
        }

        for (int i = args.size(); i < t.children.size(); i++) {
            ip.eval(t.children.get(i));
        }
        ip.env = prev;
    }
}
