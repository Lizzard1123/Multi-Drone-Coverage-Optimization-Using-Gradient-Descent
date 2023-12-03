package helpers;

public class Coords {
    //in
    private int x;
    private int y;
    public static int radius = 0;

    public Coords(int x, int y){
        this.x = x;
        this.y = y;
    }

    public static void setRadius(int newRadius){
        radius = newRadius;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public String toString(){
        return "Drone at X: " + x + ", Y: " + y;
    }

    public int hashCode(){
        return (int) (Math.pow(x, 2) + Math.pow(y, 2));
    }

    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        Coords others = (Coords) other;
        return this.x == others.getX() && this.y == others.getY();
    }
}
