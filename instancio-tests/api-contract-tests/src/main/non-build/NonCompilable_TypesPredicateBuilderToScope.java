import org.instancio.*;

class NonCompilable_TypesPredicateBuilderToScope {

    void nonCompilable() {
        // types() cannot be converted to scope
        Select.types().toScope();
    }
}