package org.instancio;

import java.util.List;

/**
 * A container for grouping multiple binding targets.
 */
public interface Binding {

    /**
     * Returns a list of targets that comprise this binding.
     *
     * @return binding targets
     */
    List<BindingTarget> getTargets();
}
