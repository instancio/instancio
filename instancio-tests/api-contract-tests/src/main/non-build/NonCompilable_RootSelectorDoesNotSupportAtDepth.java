import org.instancio.*;

class NonCompilable_RootSelectorDoesNotSupportAtDepth {

    void nonCompilable() {
        // Root selector does not support atDepth() method
        Select.root().atDepth(1);
    }
}