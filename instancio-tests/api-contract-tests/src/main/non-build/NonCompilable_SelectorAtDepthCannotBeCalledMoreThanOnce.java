import org.instancio.*;

class NonCompilable_SelectorAtDepthCannotBeCalledMoreThanOnce {

    void nonCompilable() {
        // The second call to atDepth() should produce compilation error
        Select.all(String.class).atDepth(1).atDepth(2);
    }
}