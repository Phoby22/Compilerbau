import java.util.HashMap;
import java.util.Map;

public class Enviroment {
    Enviroment enclosing;
    Map<String, Object> values;

    public Enviroment(Enviroment env) {
        enclosing = env;
        values = new HashMap<>();
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(String name) {
        if (values.get(name) != null) {
            return values.get(name);
        } else if (enclosing.values != null){
            return enclosing.values.get(name);
        }
        System.out.printf("%s wurde nicht im Scope definiert!\n", name);
        return null;
    }
}
