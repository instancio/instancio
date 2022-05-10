import org.instancio.*;

class NonCompilable_PassingScopeAsSelector {

    void nonCompilable() {
        // Passing scope as a selector
        Instancio.of(String.class).set(Select.scope(String.class), "foo");
    }
}