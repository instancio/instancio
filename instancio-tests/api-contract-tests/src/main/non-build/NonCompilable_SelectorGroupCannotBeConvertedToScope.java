import org.instancio.*;

class NonCompilable_SelectorGroupCannotBeConvertedToScope {

    void nonCompilable() {
        Select.all(String.class).toScope(); // OK! regular selector can be converted to scope

        // Group has toScope() method
        Select.all(Select.allStrings()).toScope();
    }
}