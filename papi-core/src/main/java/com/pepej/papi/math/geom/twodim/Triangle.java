package com.pepej.papi.math.geom.twodim;

public abstract class Triangle implements Figure2D {

    public static class RightTriangle extends Triangle {

        @Override
        public double getSquare() {
            return (a * b) * 0.5;
        }

        @Override
        public double getPerimeter() {
            return a + b + c;
        }

        private final double a;
        private final double b;
        private final double c;

        public RightTriangle(double a, double b) {
            this.a = a;
            this.b = b;
            this.c = Math.sqrt(a * a + b * b); // c = √(a^2 + b^2)
        }


        public double getHypot() {
            return c;
        }

        public double getA() {
            return a;
        }

        public double getB() {
            return b;
        }
    }

    public static class EquilateralTriangle extends Triangle {

        private final double a;

        public EquilateralTriangle(final double a) {
            this.a = a;
        }

        public double getHeight() {
            return (Math.sqrt(3) * a) * 0.5; // (1/2) * √3 * a
        }

        @Override
        public double getSquare() {
            return (Math.sqrt(3) * (a * a)) * 0.25; //(1/4) * √3 * a^2
        }

        @Override
        public double getPerimeter() {
            return 3 * a;
        }

        public double getA() {
            return a;
        }
    }

    public static class IsoscelesTriangle extends Triangle {

        private final double a;
        private final double b;

        public IsoscelesTriangle(final double a, final double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double getSquare() {
            return (b * 0.25) * Math.sqrt(4 * (a * a) - (b * b)); // (b/4) * √(4a^2 - b^2)
        }

        @Override
        public double getPerimeter() {
            return 2 * a + b;
        }

        public double getB() {
            return b;
        }

        public double getA() {
            return a;
        }
    }
}
