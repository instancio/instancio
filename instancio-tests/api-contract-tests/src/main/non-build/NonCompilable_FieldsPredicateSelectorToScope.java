import org.instancio.*;

class NonCompilable_FieldsPredicateSelectorToScope {

    void nonCompilable() {
        // predicate selector cannot be converted to scope
        Select.fields(p -> true).toScope();
    }
}