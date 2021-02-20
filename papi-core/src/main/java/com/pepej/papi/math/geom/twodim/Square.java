package com.pepej.papi.math.geom.twodim;

public class Square implements Figure2D {

    private final double a;
    private final double diagonal;

    public Square(double a) {
        this.a = a;
        this.diagonal = a * Math.sqrt(2);
    }

    @Override
    public double getSquare() {
        return (a * a);
    }

    @Override
    public double getPerimeter() {
        return 4*a;
    }

    public double getCenter() {
        return diagonal * 0.5; //centre of diagonal
    }
}