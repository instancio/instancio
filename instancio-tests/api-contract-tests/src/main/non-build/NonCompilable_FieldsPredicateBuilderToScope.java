import org.instancio.*;

class NonCompilable_FieldsPredicateBuilderToScope {

    void nonCompilable() {
        // fields() cannot be converted to scope
        Select.fields().toScope();
    }
}