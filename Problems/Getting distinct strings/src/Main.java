import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


class Operator {

    public static UnaryOperator<List<String>> unaryOperator = data -> new ArrayList<>(new HashSet<>(data));
}