import org.instancio.*;

class NonCompilable_OfMapWithTypeParameters {

    void nonCompilable() {
        // Cannot use withTypeParameters() with ofMap()
        Instancio.ofMap(String.class, String.class)
                .withTypeParameters(String.class, String.class);
    }
}