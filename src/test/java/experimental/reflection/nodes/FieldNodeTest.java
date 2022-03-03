package experimental.reflection.nodes;

import org.junit.jupiter.api.Test;
import org.instancio.pojo.generics.MiscFields;

import java.lang.reflect.Field;

class FieldNodeTest {

    @Test
    void printAllFieldNode() {

        for (Field field : MiscFields.class.getDeclaredFields()) {
            FieldNode fieldNode = new FieldNode(field);
            System.out.println(fieldNode);

        }

//        ReflectionUtils.getParameterizedTypes(ReflectionUtils.getField(MiscFields.class, "fooBarBazString"));
    }
}