import org.instancio.*;

class NonCompilable_NestedGroups {

    void nonCompilable() {
        // Cannot create nested groups
        Select.all(Select.all(Select.field("foo")));
    }
}