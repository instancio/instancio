package experimental.reflection.nodes;

import org.instancio.pojo.generics.MiscFields;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ClassNodeTest {


    @Test
    void test() {
        Map<String, Class<?>> typeMap = new HashMap<>();
        typeMap.put("A", Long.class);
        typeMap.put("B", String.class);
        typeMap.put("C", Integer.class);

        final ClassNode node = new ClassNode(MiscFields.class, typeMap);

        System.out.println(node);
    }
}
