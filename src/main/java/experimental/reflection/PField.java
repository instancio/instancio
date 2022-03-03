package experimental.reflection;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.StringJoiner;

public class PField {
    Field field;
    PClass pFieldType; // item
    Deque<Class<?>> genericTypes; // [String]

    @Override
    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
            return new StringJoiner("\n        ", "{", "}")
                    .add(field.getName() + "<" + field.getGenericType().getTypeName() + ">" + " => " + pFieldType)
                    .add("genericTypes=" + (genericTypes.isEmpty() ? "none" : genericTypes.peekLast()))
                    .toString();
    }
}
