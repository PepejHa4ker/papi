package com.pepej.papi.math.geom.twodim;

public class Rectangle implements Figure2D {

    private final double a;
    private final double b;
    private final double diagonal;

    public Rectangle(final double a, final double b) {
        this.a = a;
        this.b = b;
        this.diagonal = Math.sqrt(a*a + b*b);
    }

    @Override
    public double getSquare() {
        return a*b;
    }

    public double getDiagonal() {
        return diagonal;
    }

    @Override
    public double getPerimeter() {
        return (a+b) * 2;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }
}
