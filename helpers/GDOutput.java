package helpers;

import java.io.FileWriter;
import java.io.IOException;

public class GDOutput {
    private Coords[][] coords;
    private double[] coverageOverTime;
    private double[][] individualHistory;

    public GDOutput(Coords[][] coords, double[] coverageOverTime, double[][] individualHistory){
        this.coords = coords;
        this.coverageOverTime = coverageOverTime;
        this.individualHistory = individualHistory;
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

    public void printIndividualHistory(){
        System.out.println("------- INDIVIDUAL HISTORY -------");
        System.out.println("[");
        for(int k = 0; k < coords.length; k++){
            //for each location coordinates, print it out
            System.out.println("[");
            for(int i = 0; i < coords[k].length; i++){
                System.out.print("[" + individualHistory[k][i] + "]");
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

    public void createFile(double stepSize, int numDrones, int radius, int width, int imageSize, String name){
        // JSON string construction
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("const globalData = ");
        jsonBuilder.append("{");
        //variables
        //stepSize, numDrones, Coords.radius ,stepSize, width, image.getSize()
        jsonBuilder.append("\"stepSize\": " + stepSize + ",");
        jsonBuilder.append("\"numDrones\": " + numDrones + ",");
        jsonBuilder.append("\"radius\": " + radius + ",");
        jsonBuilder.append("\"width\": " + width + ",");
        jsonBuilder.append("\"imageSize\": " + imageSize + ",");
        jsonBuilder.append("\"imageName\": " + "\""+ name + "\"" + ",");
        //coverage
        jsonBuilder.append("\"coverage\": ");
        jsonBuilder.append("[");
        for(int i = 0; i < coverageOverTime.length; i++){
            jsonBuilder.append("[" + coverageOverTime[i] + "]");
            if(i != coverageOverTime.length - 1){
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");

        //paths
        jsonBuilder.append("\"positions\": ");
        jsonBuilder.append("[");
        for(int k = 0; k < coords.length; k++){
            //for each location coordinates, print it out
            jsonBuilder.append("[");
            for(int i = 0; i < coords[k].length; i++){
                jsonBuilder.append("[" + coords[k][i].getX() + ", " + coords[k][i].getY() + "]");
                if(i != coords[k].length - 1){
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            if(k != coords.length - 1){
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("],");
        //history
        jsonBuilder.append("\"individualHistory\": ");
        jsonBuilder.append("[");
        for(int k = 0; k < coords.length; k++){
            //for each location coordinates, print it out
            jsonBuilder.append("[");
            for(int i = 0; i < coords[k].length; i++){
                jsonBuilder.append("[" + individualHistory[k][i] + "]");
                if(i != coords[k].length - 1){
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            if(k != coords.length - 1){
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        jsonBuilder.append("}");

        // Writing JSON to a file
        try (FileWriter fileWriter = new FileWriter("./website/output.js")) {
            fileWriter.write(jsonBuilder.toString());
            //System.out.println("Array successfully written to output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
