import org.instancio.*;

class NonCompilable_CannotChainWithinMethods {

    void nonCompilable() {
        // Cannot chain "within()" methods
        Select.field("foo")
                .within(Select.scope(String.class))
                .within(Select.scope(String.class));
    }
}