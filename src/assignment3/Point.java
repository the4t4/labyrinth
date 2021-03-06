package assignment3;

public class Point{
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point addDirection(Direction d){
        return new Point(x+d.x, y+d.y);
    }
    
    @Override 
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Point)) return false;
        Point other = (Point)obj;
        return this.x == other.x && this.y == other.y;
    }
}

