import org.instancio.*;

class NonCompilable_SelectorGroupDoesNotSupportAtDepth {

    void nonCompilable() {
        // Group does not support atDepth() method
        Select.all(Select.allStrings()).atDepth(1);
    }
}