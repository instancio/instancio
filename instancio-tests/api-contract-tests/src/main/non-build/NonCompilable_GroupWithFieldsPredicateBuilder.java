import org.instancio.*;

class NonCompilable_GroupWithFieldsPredicateBuilder {

    void nonCompilable() {
        // Predicate builder for fields is not groupable
        Select.all(Select.fields());
    }
}