import org.instancio.*;

class NonCompilable_OfListWithTypeParameters {

    void nonCompilable() {
        // Cannot use withTypeParameters() with ofList()
        Instancio.ofList(String.class)
                .withTypeParameters(String.class);
    }
}