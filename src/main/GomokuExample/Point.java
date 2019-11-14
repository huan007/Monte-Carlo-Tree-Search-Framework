package GomokuExample;

public class Point {
    public int x;
    public int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;
        else {
            Point otherPoint = (Point) obj;
            // Same coordinates -> Equals
            if ((x == otherPoint.x) && (y == otherPoint.y))
                return true;
            // Different Coordinates
            else return false;
        }
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
