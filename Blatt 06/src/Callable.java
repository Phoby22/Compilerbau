import java.util.List;

public interface Callable {
    public void call(Interpreter i, List<Object> args);
}
