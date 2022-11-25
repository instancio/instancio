import org.instancio.*;

class NonCompilable_TypesPredicateBuilderBuildMethod {

    void nonCompilable() {
        // types() builder should not expose build() method
        Select.types().build();
    }
}