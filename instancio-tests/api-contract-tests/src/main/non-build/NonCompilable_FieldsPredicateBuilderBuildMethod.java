import org.instancio.*;

class NonCompilable_FieldsPredicateBuilderBuildMethod {

    void nonCompilable() {
        // fields() builder should not expose build() method
        Select.fields().build();
    }
}