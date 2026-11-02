package Vector;

import static java.lang.Math.pow;

public class Vec2 {
        private final double x;
        private final double y;

        public Vec2(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Vec2 add(Vec2 other) {
            return new Vec2(
                    this.x + other.x,
                    this.y + other.y
            );
        }

        public Vec2 sub(Vec2 other) {
            return new Vec2(
                    this.x - other.x,
                    this.y - other.y
            );
        }

    public Vec2 mul(double s) {
        return new Vec2(
                this.x * s,
                this.y * s
        );
    }

    public Vec2 mulSep(double xs, double ys){
        return new Vec2(
                this.x * xs,
                this.y * ys
        );
    }

    public double dot(Vec2 other){
        return  this.x * other.x + this.y * other.y;
    }

    public double lengthSq() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSq());
    }

    public Vec2 toUnit() {
        double length = length();
        if (length == 0) {
            throw new ArithmeticException("Cannot normalize zero-length vector");
        }
        return new Vec2(x / length, y / length);
    }

    public double distSq(Vec2 other){
        return pow(this.x - other.x, 2) + pow(this.y - other.y, 2);

    }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

    public static Vec2 parseVec2(String s) {
        String[] parts = s.trim().split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid Vector2D format. Expected: x,y"
            );
        }

        double x = Double.parseDouble(parts[0].trim());
        double y = Double.parseDouble(parts[1].trim());

        return new Vec2(x, y);
    }
}
