import org.instancio.*;

class NonCompilable_OfSetWithTypeParameters {

    void nonCompilable() {
        // Cannot use withTypeParameters() with ofSet()
        Instancio.ofSet(String.class)
                .withTypeParameters(String.class);
    }
}