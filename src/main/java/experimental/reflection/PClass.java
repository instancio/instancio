package experimental.reflection;

import org.instancio.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PClass {
    private final Class<?> klass;
    private final List<PField> pFields = new ArrayList<>();

    public PClass(Class<?> klass) {
        this.klass = klass;
    }

    public static PClass from(Class<?> klass) {
        final PClass pClass = new PClass(klass);

        final Field[] declaredFields = klass.getDeclaredFields();
        for (Field field : declaredFields) {
            PField pField = new PField();
            pField.field = field;
            pField.pFieldType = PClass.from(field.getType());
            pField.genericTypes = ReflectionUtils.getParameterizedTypes(field.getGenericType());

            pClass.pFields.add(pField);
        }

        return pClass;
    }

    @Override
    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
            return new StringJoiner("\n   ", klass.getSimpleName(), "")
                    .add(pFields.toString())
                    .toString();
    }
}
