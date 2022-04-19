package org.instancio;

/**
 * A class for targeting fields and classes.
 */
public interface BindingTarget {

    /**
     * Type of this binding target.
     *
     * @return binding type
     */
    BindingType getType();

    /**
     * Returns the target class to bind to.
     *
     * @return target class
     */
    Class<?> getTargetClass();

    /**
     * Returns the field to bind to.
     *
     * @return target field name
     */
    String getFieldName();

    /**
     * Supported binding types.
     */
    enum BindingType {

        /**
         * Binding to a class.
         */
        TYPE,

        /**
         * Binding to a field.
         */
        FIELD
    }
}
