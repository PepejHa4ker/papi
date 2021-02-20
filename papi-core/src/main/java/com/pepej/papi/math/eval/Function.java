package com.pepej.papi.math.eval;

public class Function {
    private final String name;
    private final int minArgumentCount;
    private final int maxArgumentCount;

    /**
     * Constructor.
     * <br>This constructor builds a function with a fixed arguments count.
     *
     * @param name          The function's name
     * @param argumentCount The function's argument count.
     * @throws IllegalArgumentException if argumentCount is lower than 0 or if the function name is null or empty.
     */
    public Function(String name, int argumentCount) {
        this(name, argumentCount, argumentCount);
    }

    /**
     * Constructor.
     * <br>This constructor builds a function with a variable arguments count.
     * <br>For instance, a minimum function may have at least one argument.
     *ц
     * @param name             The function's name
     * @param minArgumentCount The function's minimum argument count.
     * @param maxArgumentCount The function's maximum argument count (Integer.MAX_VALUE to specify no upper limit).
     * @throws IllegalArgumentException if minArgumentCount is less than 0 or greater than maxArgumentCount or if the function name is null or empty.
     */
    public Function(String name, int minArgumentCount, int maxArgumentCount) {
        if ((minArgumentCount < 0) || (minArgumentCount > maxArgumentCount)) {
            throw new IllegalArgumentException("Invalid argument count");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid function name");
        }
        this.name = name;
        this.minArgumentCount = minArgumentCount;
        this.maxArgumentCount = maxArgumentCount;
    }

    /**
     * Gets the function's name.
     *
     * @return the name of the function
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the function's minimum argument count.
     *
     * @return an integer
     */
    public int getMinimumArgumentCount() {
        return this.minArgumentCount;
    }

    /**
     * Gets the function's maximum argument count.
     *
     * @return an integer
     */
    public int getMaximumArgumentCount() {
        return this.maxArgumentCount;
    }
}
