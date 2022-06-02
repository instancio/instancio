import org.instancio.*;

class NonCompilable_SelectorWithinSelector {

    void nonCompilable() {
        // Cannot pass 'Selector' to 'within()'
        Select.all(String.class).within(Select.all(String.class));
    }
}