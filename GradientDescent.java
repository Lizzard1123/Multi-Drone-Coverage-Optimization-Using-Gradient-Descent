import java.util.Arrays;

import helpers.Coords;
import helpers.GDGifOutput;
import helpers.GDOutput;
import helpers.Pixels;
import helpers.PreciseCoords;

public class GradientDescent {
    public Pixels image;
    private int popNumber = 5;
    private double popBar = 0.70;

    public GradientDescent(Pixels image){
        this.image = image;
    }

    public PreciseCoords[] getAllGradients(Coords[] locations){
        PreciseCoords[] derivatives = new PreciseCoords[locations.length];
        int step = 1;
        for(int i = 0; i < derivatives.length; i++){
            PreciseCoords coordPartials = new PreciseCoords(0, 0);
            //change in i.x
            locations[i].setX(locations[i].getX() - step);
            double previousCoverage = image.coverage(locations);
            locations[i].setX(locations[i].getX() + 2 * step);
            double newCoverage = image.coverage(locations);
            double change = (newCoverage - previousCoverage) / (2 * step);
            coordPartials.setX(change);
            locations[i].setX(locations[i].getX() - step);

            //change in i.y
            locations[i].setY(locations[i].getY() - step);
            previousCoverage = image.coverage(locations);
            locations[i].setY(locations[i].getY() + 2 * step);
            newCoverage = image.coverage(locations);
            change = (newCoverage - previousCoverage) / (2 * step);
            coordPartials.setY(change);
            locations[i].setY(locations[i].getY() - step);

            //add cord
            derivatives[i] = coordPartials;
        }
        return derivatives;
    }

    public void printDerivatives(PreciseCoords[] derivs){
        System.out.println("[");
        for(int i = 0; i < derivs.length; i++){
            System.out.print("[" + derivs[i].getX() + ", " + derivs[i].getY() + "]");
            if(i != derivs.length - 1){
              System.out.print(",");
            }
            System.out.println();
        }
        System.out.print("]");
    }

    public GDOutput start(int iterations, int numDrones, double stepSize, Coords[] prev){
        //start in random locations
        Coords[][] history = new Coords[iterations][numDrones];
        double[][] individualHistory = new double[iterations][numDrones];
        Coords[] locations;
        if(prev == null){
            locations = image.randomPositions(numDrones);
        } else {
            locations = image.warmStart(prev);
        }
        double[] coverageOverTime = new double[iterations];
        for(int i = 0; i < iterations; i++){
            if(i == iterations - 1){
                //pick best performer and put it at the end
                int bestIndex = 0;
                double bestCoverage = coverageOverTime[bestIndex];
                for(int j = 1; j < coverageOverTime.length - 1; j++){
                    if(coverageOverTime[j] >= bestCoverage){
                        bestCoverage = coverageOverTime[j];
                        bestIndex = j;
                    }
                }
                //update arrays
                for(int j = 0; j < numDrones; j++){
                    history[i][j] = new Coords(history[bestIndex][j].getX(), history[bestIndex][j].getY());
                }
                for(int j = 0; j < numDrones; j++){
                    individualHistory[i][j] = individualHistory[bestIndex][j];
                }
                for(int j = 0; j < numDrones; j++){
                    locations[j] = new Coords(history[bestIndex][j].getX(), history[bestIndex][j].getY());
                }
                coverageOverTime[i] = bestCoverage;
                continue;
            }
            for(int k = 0; k < locations.length; k++){
                Coords[] onlyOneDrone = {locations[k]};
                double onlyOneCoverage = image.coverage(onlyOneDrone);
                individualHistory[i][k] = onlyOneCoverage;
            }
            if(i % (iterations / popNumber) == 0){
                int highestIndex = 0;
                double highestCoverage = individualHistory[i][highestIndex];
                int lowestIndex = 0;
                double lowestCoverage = individualHistory[i][lowestIndex];
                for(int k = 1; k < individualHistory[i].length; k++){
                    if(individualHistory[i][k] > highestCoverage){
                        highestCoverage = individualHistory[i][k];
                        highestIndex = k;
                    }
                    if(individualHistory[i][k] < lowestCoverage){
                        lowestCoverage = individualHistory[i][k];
                        lowestIndex = k;
                    }
                }
                if(lowestCoverage / highestCoverage <= popBar){
                    int newX = locations[highestIndex].getX() + 1;
                    int newY = locations[highestIndex].getY() + 1;
                    locations[lowestIndex].setX(newX);
                    locations[lowestIndex].setY(newY);
                }
            }
            //get Gradient matrix of coords
            PreciseCoords[] gradient = getAllGradients(locations);
            //update locations
            double diminishingStep = (Math.exp(-((double) i) / 30) * 3 + 1) * stepSize;
            for(int j = 0; j < numDrones; j++){
                int newX = (int) Math.round(locations[j].getX() + (diminishingStep * gradient[j].getX()));
                locations[j].setX(newX);
                int newY = (int) Math.round(locations[j].getY() + (diminishingStep * gradient[j].getY()));
                locations[j].setY(newY);
            }
            // check new positions
            coverageOverTime[i] = image.coverage(locations);
            //copy into history
            for(int k = 0; k < locations.length; k++){
                history[i][k] = new Coords(locations[k].getX(), locations[k].getY());
            }
        }
        return new GDOutput(history, coverageOverTime, individualHistory, image.getName());
    }

    public GDOutput start(int iterations, int numDrones, double stepSize){
        return start(iterations, numDrones, stepSize, null);
    }


    public GDGifOutput start(int frames, int iterations, int numDrones, double stepSize, boolean warm){
        GDOutput[] outputs = new GDOutput[frames];
        for(int i = 0; i < frames; i++){
            //make new image
            String newIndex = "";
            int num = i;
            if(num < 100){
                newIndex += "0";
            }
            if(num < 10){
                newIndex += "0";
            }
            newIndex += num;
            String newString = "./frames/frame_" + newIndex + "_delay-0.04s.jpg";
            Pixels newImage = new Pixels(image.width, newString);
            this.image = newImage;

            if(i == 0 || !warm){
                outputs[i] = start(iterations, numDrones, stepSize);
            } else {
                outputs[i] = start(iterations, numDrones, stepSize, outputs[i - 1].getBestCoords());
            }
        }
        return new GDGifOutput(outputs);
    }

    public GDGifOutput start(int frames, int iterations, int numDrones, double stepSize){
        return start(frames, iterations, numDrones, stepSize, false);
    }

    public GDGifOutput startThreads(int threads, int frames, int iterations, int numDrones, double stepSize){
        ThreadHandler threadHandler = new ThreadHandler(threads);
        GDOutput[] outputs = threadHandler.start(iterations, numDrones, stepSize, frames, image);
        return new GDGifOutput(outputs);
    }
}
