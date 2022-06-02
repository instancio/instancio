import org.instancio.*;

class NonCompilable_PassingGroupToWithin {

    void nonCompilable() {
        // Cannot pass 'SelectorGroup' to 'within()'
        Select.field("foo").within(Select.all(Select.field("bar")));
    }
}