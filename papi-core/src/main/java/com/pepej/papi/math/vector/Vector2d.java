package com.pepej.papi.math.vector;

import com.pepej.papi.math.GenericMath;
import com.pepej.papi.math.HashFunctions;
import com.pepej.papi.math.TrigonometricMath;

import java.io.Serializable;
import java.util.Random;

public class Vector2d implements Vectord, Comparable<Vector2d>, Serializable, Cloneable {
    private static final long serialVersionUID = 1;
    public static final Vector2d ZERO = new Vector2d(0, 0);
    public static final Vector2d UNIT_X = new Vector2d(1, 0);
    public static final Vector2d UNIT_Y = new Vector2d(0, 1);
    public static final Vector2d ONE = new Vector2d(1, 1);
    private final double x;
    private final double y;
    private transient volatile boolean hashed = false; //lazy hashcode computation
    private transient volatile int hashCode = 0; //lazy hashcode computation

    public Vector2d() {
        this(0, 0);
    }

    public Vector2d(Vector2d v) {
        this(v.x, v.y);
    }

    public Vector2d(Vector3d v) {
        this(v.getX(), v.getY());
    }

    public Vector2d(Vector4d v) {
        this(v.getX(), v.getY());
    }

    public Vector2d(VectorNd v) {
        this(v.get(0), v.get(1));
    }

    public Vector2d(float x, float y) {
        this((double) x, y);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getFloorX() {
        return GenericMath.floor(x);
    }

    public int getFloorY() {
        return GenericMath.floor(y);
    }

    public Vector2d add(Vector2d v) {
        return add(v.x, v.y);
    }

    public Vector2d add(float x, float y) {
        return add((double) x, y);
    }

    public Vector2d add(int x, int y) {
        return add((double) x, y);
    }

    public Vector2d add(long x, long y) {
        return add((double) x, y);
    }

    public Vector2d add(double x, double y) {
        return new Vector2d(this.x + x, this.y + y);
    }

    public Vector2d sub(Vector2d v) {
        return sub(v.x, v.y);
    }

    public Vector2d sub(float x, float y) {
        return sub((double) x, y);
    }

    public Vector2d sub(int x, int y) {
        return sub((double) x, y);
    }

    public Vector2d sub(long x, long y) {
        return sub((double) x, y);
    }

    public Vector2d sub(double x, double y) {
        return new Vector2d(this.x - x, this.y - y);
    }

    public Vector2d mul(float a) {
        return mul((double) a);
    }

    @Override
    public Vector2d mul(double a) {
        return mul(a, a);
    }


    public Vector2d mul(Vector2d v) {
        return mul(v.x, v.y);
    }

    public Vector2d mul(float x, float y) {
        return mul((double) x, y);
    }

    public Vector2d mul(double x, double y) {
        return new Vector2d(this.x * x, this.y * y);
    }

    public Vector2d div(float a) {
        return div((double) a);
    }

    @Override
    public Vector2d div(double a) {
        return div(a, a);
    }

    public Vector2d div(Vector2d v) {
        return div(v.x, v.y);
    }

    public Vector2d div(float x, float y) {
        return div((double) x, y);
    }

    public Vector2d div(double x, double y) {
        return new Vector2d(this.x / x, this.y / y);
    }

    public double dot(Vector2d v) {
        return dot(v.x, v.y);
    }

    public double dot(Vector2f v) {
        return dot(v.getX(), v.getY());
    }

    public double dot(float x, float y) {
        return dot((double) x, y);
    }

    public double dot(double x, double y) {
        return this.x * x + this.y * y;
    }

    public Vector2d pow(float pow) {
        return pow((double) pow);
    }

    @Override
    public Vector2d pow(double power) {
        return new Vector2d(Math.pow(x, power), Math.pow(y, power));
    }

    @Override
    public Vector2d ceil() {
        return new Vector2d(Math.ceil(x), Math.ceil(y));
    }

    @Override
    public Vector2d floor() {
        return new Vector2d(GenericMath.floor(x), GenericMath.floor(y));
    }

    @Override
    public Vector2d round() {
        return new Vector2d(Math.round(x), Math.round(y));
    }

    @Override
    public Vector2d abs() {
        return new Vector2d(Math.abs(x), Math.abs(y));
    }

    @Override
    public Vector2d negate() {
        return new Vector2d(-x, -y);
    }

    public Vector2d min(Vector2d v) {
        return min(v.x, v.y);
    }

    public Vector2d min(float x, float y) {
        return min((double) x, y);
    }

    public Vector2d min(double x, double y) {
        return new Vector2d(Math.min(this.x, x), Math.min(this.y, y));
    }

    public Vector2d max(Vector2d v) {
        return max(v.x, v.y);
    }

    public Vector2d max(float x, float y) {
        return max((double) x, y);
    }

    public Vector2d max(double x, double y) {
        return new Vector2d(Math.max(this.x, x), Math.max(this.y, y));
    }

    public double lengthSquared(Vector2d v) {
        return lengthSquared(v.x, v.y);
    }

    public double lengthSquared(float x, float y) {
        return lengthSquared((double) x, y);
    }

    public double lengthSquared(double x, double y) {
        final double dx = this.x - x;
        final double dy = this.y - y;
        return dx * dx + dy * dy;
    }

    public double length(Vector2d v) {
        return length(v.x, v.y);
    }

    public double length(float x, float y) {
        return length((double) x, y);
    }

    public double length(double x, double y) {
        return Math.sqrt(lengthSquared(x, y));
    }

    @Override
    public double lengthSquared() {
        return x * x + y * y;
    }

    @Override
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double scalar(Vector2d other) {
        double mul = this.dot(other);
        double length = this.length() * other.length();
        return mul / length;
    }

    public double scalar(Vector2f other) {
        double mul = this.dot(other);
        double length = this.length() * other.length();
        return mul / length;
    }

    @Override
    public Vector2d normalize() {
        final double length = length();
        if (Math.abs(length) < GenericMath.DBL_EPSILON) {
            return Vector2d.ZERO;
        }
        return new Vector2d(x / length, y / length);
    }

    /**
     * Return the axis with the minimal value.
     *
     * @return {@link int} axis with minimal value
     */
    @Override
    public int getMinAxis() {
        return x < y ? 0 : 1;
    }

    /**
     * Return the axis with the maximum value.
     *
     * @return {@link int} axis with maximum value
     */
    @Override
    public int getMaxAxis() {
        return x > y ? 0 : 1;
    }

    public Vector3d toVector3() {
        return toVector3(0);
    }

    public Vector3d toVector3(float z) {
        return toVector3((double) z);
    }

    public Vector3d toVector3(double z) {
        return new Vector3d(this, z);
    }

    public Vector4d toVector4() {
        return toVector4(0, 0);
    }

    public Vector4d toVector4(float z, float w) {
        return toVector4((double) z, w);
    }

    public Vector4d toVector4(double z, double w) {
        return new Vector4d(this, z, w);
    }

    public VectorNd toVectorN() {
        return new VectorNd(this);
    }

    @Override
    public double[] toArray() {
        return new double[]{x, y};
    }

    @Override
    public Vector2i toInt() {
        return new Vector2i(x, y);
    }

    @Override
    public Vector2l toLong() {
        return new Vector2l(x, y);
    }

    @Override
    public Vector2f toFloat() {
        return new Vector2f(x, y);
    }

    @Override
    public int compareTo(Vector2d v) {
        return (int) Math.signum(lengthSquared() - v.lengthSquared());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector2d)) {
            return false;
        }
        final Vector2d vector2 = (Vector2d) o;
        if (Double.compare(vector2.x, x) != 0) {
            return false;
        }
        return Double.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        if (!hashed) {
            final int result = (x != +0.0f ? HashFunctions.hash(x) : 0);
            hashCode = 31 * result + (y != +0.0f ? HashFunctions.hash(y) : 0);
            hashed = true;
        }
        return hashCode;
    }

    @Override
    public Vector2d clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Vector2d(this);
    }

    @Override
    public String toString() {
        return "Vector2d(" + x + ", " + y + ")";
    }

    /**
     * Gets the direction vector of a random angle using the random specified.
     *
     * @param random to use
     * @return the random direction vector
     */
    public static Vector2d createRandomDirection(Random random) {
        return createDirectionRad(random.nextDouble() * TrigonometricMath.TWO_PI);
    }

    /**
     * Gets the direction vector of a certain angle in degrees.
     *
     * @param angle in degrees
     * @return the direction vector
     */
    public static Vector2d creatDirectionDeg(float angle) {
        return createDirectionDeg(angle);
    }

    /**
     * Gets the direction vector of a certain angle in degrees.
     *
     * @param angle in degrees
     * @return the direction vector
     */
    public static Vector2d createDirectionDeg(double angle) {
        return createDirectionRad(Math.toRadians(angle));
    }

    /**
     * Gets the direction vector of a certain angle in radians.
     *
     * @param angle in radians
     * @return the direction vector
     */
    public static Vector2d createDirectionRad(float angle) {
        return createDirectionRad((double) angle);
    }

    /**
     * Gets the direction vector of a certain angle in radians.
     *
     * @param angle in radians
     * @return the direction vector
     */
    public static Vector2d createDirectionRad(double angle) {
        return new Vector2d(TrigonometricMath.cos(angle), TrigonometricMath.sin(angle));
    }
}
