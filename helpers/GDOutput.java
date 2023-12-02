package helpers;

public class GDOutput {
    private Coords[][] coords;
    private double[] coverageOverTime;

    public GDOutput(Coords[][] coords, double[] coverageOverTime){
        this.coords = coords;
        this.coverageOverTime = coverageOverTime;
    }

    public Coords[][] getCoords(){
        return this.coords;
    }

    public Coords[] getBestCoords(){
        return this.coords[this.coords.length - 1];
    }

    public double[] getCoverageOverTime(){
        return this.coverageOverTime;
    }

    public void printChanges(){
        System.out.println("[");
        for(int i = 0; i < coverageOverTime.length; i++){
            System.out.print("[" + coverageOverTime[i] + "]");
            if(i != coverageOverTime.length - 1){
              System.out.print(",");
            }
            System.out.println();
        }
        System.out.print("]");
    }

    public void printHistory(){
        System.out.println("------- HISTORY -------");
        System.out.println("[");
        for(int k = 0; k < coords.length; k++){
            //for each location coordinates, print it out
            System.out.println("[");
            for(int i = 0; i < coords[k].length; i++){
                System.out.print("[" + coords[k][i].getX() + ", " + coords[k][i].getY() + "]");
                if(i != coords[k].length - 1){
                    System.out.print(",");
                }
                System.out.println();
            }
            System.out.print("]");
            if(k != coords.length - 1){
                System.out.println(",");
            } else {
                System.out.println();
            }
        }
        System.out.print("]");
    }
}
