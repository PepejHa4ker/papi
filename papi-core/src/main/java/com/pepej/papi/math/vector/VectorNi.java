package com.pepej.papi.math.vector;

import java.io.Serializable;
import java.util.Arrays;

public class VectorNi implements Vectori, Comparable<VectorNi>, Serializable, Cloneable {
    public static VectorNi ZERO_2 = new ImmutableZeroVectorN(0, 0);
    public static VectorNi ZERO_3 = new ImmutableZeroVectorN(0, 0, 0);
    public static VectorNi ZERO_4 = new ImmutableZeroVectorN(0, 0, 0, 0);
    private static final long serialVersionUID = 1;
    private final int[] vec;

    public VectorNi(int size) {
        if (size < 2) {
            throw new IllegalArgumentException("Minimum vector size is 2");
        }
        vec = new int[size];
    }

    public VectorNi(Vector2i v) {
        this(v.getX(), v.getY());
    }

    public VectorNi(Vector3i v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public VectorNi(Vector4i v) {
        this(v.getX(), v.getY(), v.getZ(), v.getW());
    }

    public VectorNi(VectorNi v) {
        this(v.vec);
    }

    public VectorNi(int... v) {
        vec = v.clone();
    }

    public int size() {
        return vec.length;
    }

    public int get(int comp) {
        return vec[comp];
    }

    public void set(int comp, int val) {
        vec[comp] = val;
    }

    public void setZero() {
        Arrays.fill(vec, 0);
    }

    public VectorNi resize(int size) {
        final VectorNi d = new VectorNi(size);
        System.arraycopy(vec, 0, d.vec, 0, Math.min(size, size()));
        return d;
    }

    public VectorNi add(VectorNi v) {
        return add(v.vec);
    }

    public VectorNi add(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] + v[comp];
        }
        return d;
    }

    public VectorNi sub(VectorNi v) {
        return sub(v.vec);
    }

    public VectorNi sub(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] - v[comp];
        }
        return d;
    }

    public VectorNi mul(double a) {
        return mul((int) a);
    }

    @Override
    public VectorNi mul(int a) {
        final int size = size();
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] * a;
        }
        return d;
    }

    public VectorNi mul(VectorNi v) {
        return mul(v.vec);
    }

    public VectorNi mul(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] * v[comp];
        }
        return d;
    }

    public VectorNi div(double a) {
        return div((int) a);
    }

    @Override
    public VectorNi div(int a) {
        final int size = size();
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] / a;
        }
        return d;
    }

    public VectorNi div(VectorNi v) {
        return div(v.vec);
    }

    public VectorNi div(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = vec[comp] / v[comp];
        }
        return d;
    }

    public int dot(VectorNi v) {
        return dot(v.vec);
    }

    public int dot(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        int d = 0;
        for (int comp = 0; comp < size; comp++) {
            d += vec[comp] * v[comp];
        }
        return d;
    }

    public VectorNi pow(double pow) {
        return pow((int) pow);
    }

    @Override
    public VectorNi pow(int power) {
        final int size = size();
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = (int) Math.pow(vec[comp], power);
        }
        return d;
    }

    @Override
    public VectorNi abs() {
        final int size = size();
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = Math.abs(vec[comp]);
        }
        return d;
    }

    @Override
    public VectorNi negate() {
        final int size = size();
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = -vec[comp];
        }
        return d;
    }

    public VectorNi min(VectorNi v) {
        return min(v.vec);
    }

    public VectorNi min(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = Math.min(vec[comp], v[comp]);
        }
        return d;
    }

    public VectorNi max(VectorNi v) {
        return max(v.vec);
    }

    public VectorNi max(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        final VectorNi d = new VectorNi(size);
        for (int comp = 0; comp < size; comp++) {
            d.vec[comp] = Math.max(vec[comp], v[comp]);
        }
        return d;
    }

    public int lengthSquared(VectorNi v) {
        return lengthSquared(v.vec);
    }

    public int lengthSquared(int... v) {
        final int size = size();
        if (size != v.length) {
            throw new IllegalArgumentException("Vector sizes must be the same");
        }
        int d = 0;
        for (int comp = 0; comp < size; comp++) {
            final int delta = vec[comp] - v[comp];
            d += delta * delta;
        }
        return d;
    }

    public int length(VectorNi v) {
        return length(v.vec);
    }

    public int length(int... v) {
        return (int) Math.sqrt(lengthSquared(v));
    }

    @Override
    public int lengthSquared() {
        final int size = size();
        int l = 0;
        for (int comp = 0; comp < size; comp++) {
            l += vec[comp] * vec[comp];
        }
        return l;
    }

    @Override
    public int length() {
        return (int) Math.sqrt(lengthSquared());
    }

    @Override
    public int getMinAxis() {
        int axis = 0;
        int value = vec[axis];
        final int size = size();
        for (int comp = 1; comp < size; comp++) {
            if (vec[comp] < value) {
                value = vec[comp];
                axis = comp;
            }
        }
        return axis;
    }

    @Override
    public int getMaxAxis() {
        int axis = 0;
        int value = vec[axis];
        final int size = size();
        for (int comp = 1; comp < size; comp++) {
            if (vec[comp] > value) {
                value = vec[comp];
                axis = comp;
            }
        }
        return axis;
    }

    public Vector2i toVector2() {
        return new Vector2i(this);
    }

    public Vector3i toVector3() {
        return new Vector3i(this);
    }

    public Vector4i toVector4() {
        return new Vector4i(this);
    }

    @Override
    public int[] toArray() {
        return vec.clone();
    }

    @Override
    public VectorNl toLong() {
        final int size = size();
        final long[] longVec = new long[size];
        for (int comp = 0; comp < size; comp++) {
            longVec[comp] = vec[comp];
        }
        return new VectorNl(longVec);
    }

    @Override
    public VectorNf toFloat() {
        final int size = size();
        final float[] floatVec = new float[size];
        for (int comp = 0; comp < size; comp++) {
            floatVec[comp] = (float) vec[comp];
        }
        return new VectorNf(floatVec);
    }

    @Override
    public VectorNd toDouble() {
        final int size = size();
        final double[] doubleVec = new double[size];
        for (int comp = 0; comp < size; comp++) {
            doubleVec[comp] = vec[comp];
        }
        return new VectorNd(doubleVec);
    }

    @Override
    public int compareTo(VectorNi v) {
        return lengthSquared() - v.lengthSquared();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VectorNi)) {
            return false;
        }
        return Arrays.equals(vec, ((VectorNi) obj).vec);
    }

    @Override
    public int hashCode() {
        return 67 * 5 + Arrays.hashCode(vec);
    }

    @Override
    public VectorNi clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new VectorNi(this);
    }

    @Override
    public String toString() {
        return "VectorNi" + Arrays.toString(vec).replace('[', '(').replace(']', ')');
    }

    private static class ImmutableZeroVectorN extends VectorNi {
        public ImmutableZeroVectorN(int... v) {
            super(v);
        }

        @Override
        public void set(int comp, int val) {
            throw new UnsupportedOperationException("You may not alter this vector");
        }
    }
}
