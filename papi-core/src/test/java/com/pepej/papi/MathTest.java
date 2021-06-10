package com.pepej.papi;


import com.pepej.papi.math.geom.twodim.Circle;
import com.pepej.papi.math.geom.twodim.Square;
import com.pepej.papi.math.geom.twodim.Triangle.RightTriangle;
import com.pepej.papi.math.vector.Vector2d;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathTest {


    @BeforeAll
    static void v() {}



    @Test
    public void circleTest() {
        Circle circle = new Circle(2.0);
        assertEquals(circle.getSquare(),12.566370614359172);
        assertEquals(circle.getPerimeter(), 12.566370614359172);
    }

    @Test
    public void squareTest() {
        Square square = new Square(10);
        assertEquals(square.getSquare(), 100);
        assertEquals(square.getPerimeter(), 40);
        assertEquals(square.getCenter(), 7.0710678118654755);
    }

    @Test
    public void rightTriangleTest() {
        RightTriangle rightTriangle = new RightTriangle(10, 20);
        assertEquals(rightTriangle.getSquare(), 100);
        assertEquals(rightTriangle.getPerimeter(), 52.3606797749979);
        assertEquals(rightTriangle.getHypot(), 22.360679774997898);
    }

    @Test
    public void scalarTest() {
        Vector2d a = new Vector2d(3,4);
        Vector2d b = new Vector2d(4,3);
        assertEquals(0.96, a.scalar(b));
    }




}
