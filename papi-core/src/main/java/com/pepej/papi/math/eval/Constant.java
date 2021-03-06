package com.pepej.papi.math.eval;

/**
 * A constant in an expression.
 * <br>Some expressions needs constants. For instance it is impossible to perform trigonometric calculus without using pi.
 * A constant allows you to use mnemonic in your expressions instead of the raw value of the constant.
 * <br>A constant for pi would be defined by :<br>
 * <code>Constant&lt;Double&gt; pi = new Constant&lt;Double&gt;("pi");</code>
 * <br>With such a constant, you will be able to evaluate the expression "sin(pi/4)"
 * @see AbstractEvaluator#evaluate(Constant, Object)
 */
public class Constant {
    private final String name;

    /** Constructor
     * @param name The mnemonic of the constant.
     * <br>The name is used in expressions to identified the constants.
     */
    public Constant(String name) {
        this.name = name;
    }

    /** Gets the mnemonic of the constant.
     * @return the id
     */
    public String getName() {
        return name;
    }
}
