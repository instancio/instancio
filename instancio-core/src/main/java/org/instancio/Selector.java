package org.instancio;

/**
 * A class for selecting fields and classes.
 */
public interface Selector {

    /**
     * Returns the {@link SelectorType} of this selector.
     *
     * @return selector type
     */
    SelectorType selectorType();

    /**
     * Returns the selected target class.
     *
     * @return target class
     */
    Class<?> getTargetClass();

    /**
     * Returns the selected field.
     *
     * @return target field name
     */
    String getFieldName();

    /**
     * Supported selector types.
     */
    enum SelectorType {

        /**
         * Class selector.
         */
        TYPE,

        /**
         * Field selector.
         */
        FIELD
    }
}