package util;

public class Coord2d {
    int x;
    int y;

    public Coord2d(){

    }
    public Coord2d(int x, int y){
        setX(x);
        setY(y);
    }
    public Coord2d(double x, double y){
        setX((int)x);
        setY((int)y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
}
