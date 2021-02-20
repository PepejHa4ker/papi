package com.pepej.papi.math.geom.twodim;

public class Circle implements Figure2D {

    private final double rad;

    public Circle(double rad) {
        this.rad = rad;
    }

    @Override
    public double getSquare() {
        return (Math.PI * (rad*rad)); // S = PI*r^2
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * rad; // S = 2PI * r
    }

    public double getRadius() {
        return rad;
    }

    public double getCircumference() {
        return 2 * (Math.PI * rad);
    }
}
