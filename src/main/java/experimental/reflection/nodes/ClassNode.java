package experimental.reflection.nodes;

import org.instancio.util.Verify;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

// ClassNode can only have type variables
public class ClassNode {

    private final Map<String, Class<?>> typeMap;
    private final Class<?> klass;
    private List<FieldNode> children;

    public ClassNode(final Class<?> klass, Map<String, Class<?>> typeMap) {
        this.klass = klass;
        this.typeMap = validateTypeMap(klass, typeMap);
        this.children = Arrays.stream(klass.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(FieldNode::new)
                .collect(toList());

    }

    private static Map<String, Class<?>> validateTypeMap(Class<?> klass, Map<String, Class<?>> typeMap) {
        final TypeVariable<?>[] typeParameters = klass.getTypeParameters();
        final String msg = String.format("Type map should map each type parameter:\ntype map: %s\ntype parameters: %s",
                typeMap, Arrays.toString(typeParameters));

        Verify.isTrue(typeParameters.length == typeMap.size(), msg);

        for (TypeVariable<?> typeVar : typeParameters) {
            Verify.isTrue(typeMap.containsKey(typeVar.getName()), msg);
        }

        return Collections.unmodifiableMap(typeMap);
    }


    @Override
    public String toString() {
        String s = "";
        s += "ClassNode: " + klass.getSimpleName() + "\n"
                + " -> typeMap: " + typeMap + "\n";
        return s;
    }


}
