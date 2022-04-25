package org.instancio;

import java.util.List;

/**
 * A container for grouping multiple selectors.
 */
public interface SelectorGroup {

    /**
     * Returns a list of selectors that comprise this group.
     *
     * @return selector targets
     */
    List<Selector> getSelectors();
}