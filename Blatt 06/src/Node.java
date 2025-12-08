import java.util.ArrayList;
import java.util.List;

public class Node {
    String type;
    String value;
    List<Node> children = new ArrayList<>();

    public Node(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Node(String type) {
        this.type = type;
    }
}
