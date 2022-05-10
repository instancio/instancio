import org.instancio.*;

class NonCompilable_SelectorGroupHasNoScope {

    void nonCompilable() {
        // Group does not have a scope() method
        Select.all(Select.allStrings()).scope();
    }
}