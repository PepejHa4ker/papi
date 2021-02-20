package com.pepej.papi.math.vector;

import com.pepej.papi.math.GenericMath;
import com.pepej.papi.math.HashFunctions;
import com.pepej.papi.math.TrigonometricMath;
import com.pepej.papi.math.imaginary.Quaterniond;
import com.pepej.papi.math.matrix.Matrix3d;

import java.io.Serializable;
import java.util.Random;

import static com.pepej.papi.math.TrigonometricMath.*;

public class Vector3d implements Vectord, Comparable<Vector3d>, Serializable, Cloneable {
    private static final long serialVersionUID = 1;
    public static final Vector3d ZERO = new Vector3d(0, 0, 0);
    public static final Vector3d UNIT_X = new Vector3d(1, 0, 0);
    public static final Vector3d UNIT_Y = new Vector3d(0, 1, 0);
    public static final Vector3d UNIT_Z = new Vector3d(0, 0, 1);
    public static final Vector3d ONE = new Vector3d(1, 1, 1);
    public static final Vector3d RIGHT = UNIT_X;
    public static final Vector3d UP = UNIT_Y;
    public static final Vector3d FORWARD = UNIT_Z;
    private final double x;
    private final double y;
    private final double z;
    private transient volatile boolean hashed = false;
    private transient volatile int hashCode = 0;

    public Vector3d() {
        this(0, 0, 0);
    }

    public Vector3d(Vector2d v) {
        this(v, 0);
    }

    public Vector3d(Vector2d v, float z) {
        this(v, (double) z);
    }

    public Vector3d(Vector2d v, double z) {
        this(v.getX(), v.getY(), z);
    }

    public Vector3d(Vector3d v) {
        this(v.x, v.y, v.z);
    }

    public Vector3d(Vector4d v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public Vector3d(VectorNd v) {
        this(v.get(0), v.get(1), v.size() > 2 ? v.get(2) : 0);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getFloorX() {
        return GenericMath.floor(x);
    }

    public int getFloorY() {
        return GenericMath.floor(y);
    }

    public int getFloorZ() {
        return GenericMath.floor(z);
    }

    public Vector3d add(Vector3d v) {
        return add(v.x, v.y, v.z);
    }

    public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d sub(Vector3d v) {
        return sub(v.x, v.y, v.z);
    }

    public Vector3d sub(double x, double y, double z) {
        return new Vector3d(this.x - x, this.y - y, this.z - z);
    }

    public Vector3d mul(float a) {
        return mul((double) a);
    }

    @Override
    public Vector3d mul(double a) {
        return mul(a, a, a);
    }

    public Vector3d mul(Vector3d v) {
        return mul(v.x, v.y, v.z);
    }

    public Vector3d mul(double x, double y, double z) {
        return new Vector3d(this.x * x, this.y * y, this.z * z);
    }

    public Vector3d div(float a) {
        return div((double) a);
    }

    @Override
    public Vector3d div(double a) {
        return div(a, a, a);
    }

    public Vector3d div(Vector3d v) {
        return div(v.x, v.y, v.z);
    }


    public Vector3d div(double x, double y, double z) {
        return new Vector3d(this.x / x, this.y / y, this.z / z);
    }

    public double dot(Vector3d v) {
        return dot(v.getX(), v.getY(), v.getZ());
    }

    public double dot(Vector3f v) {
        return dot(v.getX(), v.getY(), v.getZ());
    }

    public double dot(double x, double y, double z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public Vector3d cross(Vector3d v) {
        return cross(v.x, v.y, v.z);
    }


    public Vector3d cross(double x, double y, double z) {
        return new Vector3d(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
    }

    public Vector3d pow(float pow) {
        return pow((double) pow);
    }

    @Override
    public Vector3d pow(double power) {
        return new Vector3d(Math.pow(x, power), Math.pow(y, power), Math.pow(z, power));
    }

    @Override
    public Vector3d ceil() {
        return new Vector3d(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    @Override
    public Vector3d floor() {
        return new Vector3d(GenericMath.floor(x), GenericMath.floor(y), GenericMath.floor(z));
    }

    @Override
    public Vector3d round() {
        return new Vector3d(Math.round(x), Math.round(y), Math.round(z));
    }

    @Override
    public Vector3d abs() {
        return new Vector3d(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    @Override
    public Vector3d negate() {
        return new Vector3d(-x, -y, -z);
    }

    public Vector3d min(Vector3d v) {
        return min(v.x, v.y, v.z);
    }


    public Vector3d min(double x, double y, double z) {
        return new Vector3d(Math.min(this.x, x), Math.min(this.y, y), Math.min(this.z, z));
    }

    public Vector3d max(Vector3d v) {
        return max(v.x, v.y, v.z);
    }


    public Vector3d max(double x, double y, double z) {
        return new Vector3d(Math.max(this.x, x), Math.max(this.y, y), Math.max(this.z, z));
    }

    public double lengthSquared(Vector3d v) {
        return lengthSquared(v.x, v.y, v.z);
    }

    public double lengthSquared(double x, double y, double z) {
        final double dx = this.x - x;
        final double dy = this.y - y;
        final double dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public double length(Vector3d v) {
        return length(v.x, v.y, v.z);
    }

    public double length(double x, double y, double z) {
        return Math.sqrt(lengthSquared(x, y, z));
    }

    @Override
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    @Override
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double scalar(Vector3d other) {
        double mul = this.dot(other);
        double length = this.length() * other.length();
        return mul / length;
    }

    public double scalar(Vector3f other) {
        double mul = this.dot(other);
        double length = this.length() * other.length();
        return mul / length;
    }

    @Override
    public Vector3d normalize() {
        final double length = length();
        if (Math.abs(length) < GenericMath.DBL_EPSILON) {
            return Vector3d.ZERO;

        }
        return new Vector3d(x / length, y / length, z / length);
    }

    /**
     * Returns the axis with the minimal value.
     *
     * @return {@link int} axis with minimal value
     */
    @Override
    public int getMinAxis() {
        return x < y ? (x < z ? 0 : 2) : (y < z ? 1 : 2);
    }

    /**
     * Returns the axis with the maximum value.
     *
     * @return {@link int} axis with maximum value
     */
    @Override
    public int getMaxAxis() {
        return x < y ? (y < z ? 2 : 1) : (x < z ? 2 : 0);
    }

    public Vector2d toVector2() {
        return new Vector2d(this);
    }

    public Vector2d toVector2(boolean useZ) {
        return new Vector2d(x, useZ ? z : y);
    }

    public Vector4d toVector4() {
        return toVector4(0);
    }

    public Vector4d toVector4(float w) {
        return toVector4((double) w);
    }

    public Vector4d toVector4(double w) {
        return new Vector4d(this, w);
    }

    public VectorNd toVectorN() {
        return new VectorNd(this);
    }

    @Override
    public double[] toArray() {
        return new double[]{x, y, z};
    }

    @Override
    public Vector3i toInt() {
        return new Vector3i(x, y, z);
    }

    @Override
    public Vector3l toLong() {
        return new Vector3l(x, y, z);
    }

    @Override
    public Vector3f toFloat() {
        return new Vector3f(x, y, z);
    }

    @Override
    public int compareTo(Vector3d v) {
        return (int) Math.signum(lengthSquared() - v.lengthSquared());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vector3d)) {
            return false;
        }
        final Vector3d vector3 = (Vector3d) o;
        if (Double.compare(vector3.x, x) != 0) {
            return false;
        }
        if (Double.compare(vector3.y, y) != 0) {
            return false;
        }
        return Double.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        if (!hashed) {
            int result = (x != +0.0f ? HashFunctions.hash(x) : 0);
            result = 31 * result + (y != +0.0f ? HashFunctions.hash(y) : 0);
            hashCode = 31 * result + (z != +0.0f ? HashFunctions.hash(z) : 0);
            hashed = true;
        }
        return hashCode;
    }

    @Override
    public Vector3d clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Vector3d(this);
    }

    @Override
    public String toString() {
        return "Vector3d(" + x + ", " + y + ", " + z + ")";
    }

    /**
     * Rotates the vector by angle degrees around x axis using rotation matrix
     *
     * @param angle how many degrees to rotate the vector
     * @return the new vector rotated by the given angle on the x axis
     */
    public Vector3d rotateAroundAxisXDeg(double angle) {
        return rotateDeg(angle, RIGHT);

    }

    /**
     * Rotates the vector by angle degrees around x axis using rotation matrix
     *
     * @param angle how many degrees to rotate the vector
     * @return the new vector rotated by the given angle on the x axis
     */
    public Vector3d rotateAroundAxisYDeg(double angle) {
        return rotateDeg(angle, UP);

    }

    /**
     * Rotates the vector by angle degrees around z axis using rotation matrix
     *
     * @param angle how many degrees to rotate the vector
     * @return the new vector rotated by the given angle on the z axis
     */
    public Vector3d rotateAroundAxisZDeg(double angle) {
        return rotateDeg(angle, FORWARD);
    }

    /**
     * Rotates the vector by given angles degrees around given axis using rotation matrix
     *
     * @param angle how many degrees to rotate the vector
     * @param axis  the axis to rotate angle
     * @return the new vector rotated by the given angle on the given axis
     */
    public Vector3d rotateDeg(double angle, Vector3d axis) {
        Quaterniond rotation = Quaterniond.fromAngleDegAxis(angle, axis);
        return Matrix3d.createRotation(rotation).transform(this);
    }

    /**
     * Rotates the vector by given angles degrees around axes using rotation matrix
     *
     * @param angleX how many degrees to rotate the vector around x axis
     * @param angleY how many degrees to rotate the vector around y axis
     * @param angleZ how many degrees to rotate the vector around z axis
     * @return the new vector rotated by the given angles on the all axis
     */
    public Vector3d rotateDeg(double angleX, double angleY, double angleZ) {
        return this.rotateAroundAxisXDeg(angleX)
                   .rotateAroundAxisYDeg(angleY)
                   .rotateAroundAxisZDeg(angleZ);
    }

    /**
     * Rotates the vector by angle radians around x axis using rotation matrix
     *
     * @param angle how many radians to rotate the vector
     * @return the new vector rotated by the given angle on the x axis
     */
    public Vector3d rotateAroundAxisXRad(double angle) {
        return rotateRad(angle, RIGHT);

    }

    /**
     * Rotates the vector by angle radians around x axis using rotation matrix
     *
     * @param angle how many radians to rotate the vector
     * @return the new vector rotated by the given angle on the x axis
     */
    public Vector3d rotateAroundAxisYRad(double angle) {
        return rotateDeg(angle, UP);

    }

    /**
     * Rotates the vector by angle radians around z axis using rotation matrix
     *
     * @param angle how many radians to rotate the vector
     * @return the new vector rotated by the given angle on the z axis
     */
    public Vector3d rotateAroundAxisZRad(double angle) {
        return rotateDeg(angle, FORWARD);
    }

    /**
     * Rotates the vector by given angles radians around given axis using rotation matrix
     *
     * @param angle how many radians to rotate the vector
     * @param axis  the axis to rotate angle
     * @return the new vector rotated by the given angle on the given axis
     */
    public Vector3d rotateRad(double angle, Vector3d axis) {
        Quaterniond rotation = Quaterniond.fromAngleRadAxis(angle, axis);
        return Matrix3d.createRotation(rotation).transform(this);
    }

    /**
     * Rotates the vector by given angles radians around axes using rotation matrix
     *
     * @param angleX how many radians to rotate the vector around x axis
     * @param angleY how many radians to rotate the vector around y axis
     * @param angleZ how many radians to rotate the vector around z axis
     * @return the new vector rotated by the given angles on the all axis
     */
    public Vector3d rotateRad(double angleX, double angleY, double angleZ) {
        return this.rotateAroundAxisXRad(angleX)
                   .rotateAroundAxisYRad(angleY)
                   .rotateAroundAxisZRad(angleZ);
    }

    /**
     * Gets the direction vector of a random pitch and yaw using the random specified.
     *
     * @param random to use
     * @return the random direction vector
     */
    public static Vector3d createRandomDirection(Random random) {
        return createDirectionRad(random.nextDouble() * TWO_PI,
                random.nextDouble() * TWO_PI);
    }

    /**
     * Gets the direction vector of a certain theta and phi in degrees. This uses the standard math spherical coordinate system.
     *
     * @param theta in degrees
     * @param phi   in degrees
     * @return the direction vector
     */
    public static Vector3d createDirectionDeg(double theta, double phi) {
        return createDirectionRad(Math.toRadians(theta), Math.toRadians(cos(phi)));
    }

    /**
     * Gets the direction vector of a certain theta and phi in radians. This uses the standard math spherical coordinate system.
     *
     * @param theta in radians
     * @param phi   in radians
     * @return the direction vector
     */
    public static Vector3d createDirectionRad(double theta, double phi) {
        final double f = TrigonometricMath.sin(phi);
        return new Vector3d(f * cos(theta), f * sin(theta), cos(phi));
    }

}
