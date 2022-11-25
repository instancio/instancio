import org.instancio.*;

class NonCompilable_TypesPredicateSelectorToScope {

    void nonCompilable() {
        // predicate selector cannot be converted to scope
        Select.types(p -> true).toScope();
    }
}