package helpers;

import java.io.FileWriter;
import java.io.IOException;

// Pretty much data dumps the result of the Java computations into a JSON file that can be read by JS in the website
//creates a JSON representation of multiple GDOutputs
public class GDGifOutput {
    private GDOutput[] outputs;

    public GDGifOutput(GDOutput[] ouputs){
        this.outputs = ouputs;
    }

    public void createFile(double stepSize, int numDrones, int radius, int width, int imageSize){
        // JSON string construction
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("const globalData = ");
        jsonBuilder.append("[");
        for(int u = 0; u < outputs.length; u++){
            jsonBuilder.append("{");
            //variables
            //stepSize, numDrones, Coords.radius ,stepSize, width, image.getSize()
            jsonBuilder.append("\"stepSize\": " + stepSize + ",");
            jsonBuilder.append("\"numDrones\": " + numDrones + ",");
            jsonBuilder.append("\"radius\": " + radius + ",");
            jsonBuilder.append("\"width\": " + width + ",");
            jsonBuilder.append("\"imageSize\": " + imageSize + ",");
            jsonBuilder.append("\"imageName\": " + "\""+ outputs[u].name + "\"" + ",");
            //coverage
            jsonBuilder.append("\"coverage\": ");
            jsonBuilder.append("[");
            for(int i = 0; i < outputs[u].coverageOverTime.length; i++){
                jsonBuilder.append("[" + outputs[u].coverageOverTime[i] + "]");
                if(i != outputs[u].coverageOverTime.length - 1){
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("],");

            //paths
            jsonBuilder.append("\"positions\": ");
            jsonBuilder.append("[");
            for(int k = 0; k < outputs[u].coords.length; k++){
                //for each location coordinates, print it out
                jsonBuilder.append("[");
                for(int i = 0; i < outputs[u].coords[k].length; i++){
                    jsonBuilder.append("[" + outputs[u].coords[k][i].getX() + ", " + outputs[u].coords[k][i].getY() + "]");
                    if(i != outputs[u].coords[k].length - 1){
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("]");
                if(k != outputs[u].coords.length - 1){
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("],");
            //history
            jsonBuilder.append("\"individualHistory\": ");
            jsonBuilder.append("[");
            for(int k = 0; k < outputs[u].coords.length; k++){
                //for each location coordinates, print it out
                jsonBuilder.append("[");
                for(int i = 0; i < outputs[u].coords[k].length; i++){
                    jsonBuilder.append("[" + outputs[u].individualHistory[k][i] + "]");
                    if(i != outputs[u].coords[k].length - 1){
                        jsonBuilder.append(",");
                    }
                }
                jsonBuilder.append("]");
                if(k != outputs[u].coords.length - 1){
                    jsonBuilder.append(",");
                }
            }
            jsonBuilder.append("]");
            jsonBuilder.append("}");
            if(u != outputs.length - 1){
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        // Writing JSON to a file
        try (FileWriter fileWriter = new FileWriter("./website/output.js")) {
            fileWriter.write(jsonBuilder.toString());
            //System.out.println("Array successfully written to output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
