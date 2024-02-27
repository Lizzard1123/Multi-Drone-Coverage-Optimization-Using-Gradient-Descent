import java.util.Arrays;

import helpers.Coords;
import helpers.GDGifOutput;
import helpers.GDOutput;
import helpers.Pixels;
import helpers.PreciseCoords;

// you know this algorithm better than I do lol
public class GradientDescent {
    //weighted (burn) probability matrix
    public Pixels image;
    // HEURISTIC: # of times throughout the gradient descent steps that the lowest performing drone teleports to the highest performing one
    private int popNumber = 5;
    // HEURISTIC: If the lowest performer is <= 70% of Highest performer, move it
    private double popBar = 0.70;

    public GradientDescent(Pixels image){
        this.image = image;
    }

    // Hold on to your seat for this one
    // Calculates the gradient matrix (of same size 2*n) by taking the derivative of moving (separately) the X and Y of each drone (separately)
    // This "gradient" is then used to move ALL of the drones at the same time.
    // It is obsoletely not the best way, there are cases where drones both move towards each other which makes the overall performance drop
    // because the output of the coverage function is team based, this individual approach is terrible, I know lol
    public PreciseCoords[] getAllGradients(Coords[] locations){
        PreciseCoords[] derivatives = new PreciseCoords[locations.length];
        // how far to move from origin to take derivative
        int step = 1;
        for(int i = 0; i < derivatives.length; i++){
            // use Precise cords to hold in the same shape as the original coords, the derivatives with Double precision 
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

    //Debug, print the gradient matrix
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


    //Gradient Descent algorithm
    public GDOutput start(int iterations, int numDrones, double stepSize, Coords[] prev){
        //history of the entire process
        Coords[][] history = new Coords[iterations][numDrones];
        // performance history of each drone
        double[][] individualHistory = new double[iterations][numDrones];
        // final optimal locations
        Coords[] locations;
        if(prev == null){
            locations = image.randomPositions(numDrones);
        } else {
            locations = image.warmStart(prev);
        }
        // track the result of GD over iterations
        double[] coverageOverTime = new double[iterations];
        for(int i = 0; i < iterations; i++){
            // End iteration cleanup
            if(i == iterations - 1){
                //HEURISTIC pick best performer and put it at the end
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

            //Start process for the iteration

            //update individual drone performance tracker
            for(int k = 0; k < locations.length; k++){
                Coords[] onlyOneDrone = {locations[k]};
                double onlyOneCoverage = image.coverage(onlyOneDrone);
                individualHistory[i][k] = onlyOneCoverage;
            }

            // In the case that it has reached one of the benchmark checkpoints, execute the teleport heuristic
            // move lowest performer to the highest performer if it falls below a certain bar
            if(i != 0 && i % (iterations / popNumber) == 0){
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

            //update locations with a diminishing step size
            // much thanks to a one Hunter Kuperman for helping me with this decay function
            double diminishingStep = (Math.exp(-((double) i) / 30) * 3 + 1) * stepSize;
            //
            for(int j = 0; j < numDrones; j++){
                int newX = (int) Math.round(locations[j].getX() + (diminishingStep * gradient[j].getX()));
                locations[j].setX(newX);
                int newY = (int) Math.round(locations[j].getY() + (diminishingStep * gradient[j].getY()));
                locations[j].setY(newY);
            }
            // metric new positions
            coverageOverTime[i] = image.coverage(locations);
            //copy into history
            for(int k = 0; k < locations.length; k++){
                history[i][k] = new Coords(locations[k].getX(), locations[k].getY());
            }
        }
        return new GDOutput(history, coverageOverTime, individualHistory, image.getName());
    }

    //java's version of an override that makes for a simple way of overloading a function
    public GDOutput start(int iterations, int numDrones, double stepSize){
        return start(iterations, numDrones, stepSize, null);
    }

    // Same process as above, just multiple times over again.
    public GDGifOutput start(int frames, int iterations, int numDrones, double stepSize, boolean warm){
        GDOutput[] outputs = new GDOutput[frames];
        for(int i = 0; i < frames; i++){
            //make new image filename (formatting again)
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
            // check to see if this is configured for warm starting
            if(i == 0 || !warm){
                outputs[i] = start(iterations, numDrones, stepSize);
            } else {
                outputs[i] = start(iterations, numDrones, stepSize, outputs[i - 1].getBestCoords());
            }
        }
        return new GDGifOutput(outputs);
    }

    //java's version of an override that makes for a simple way of overloading a function
    public GDGifOutput start(int frames, int iterations, int numDrones, double stepSize){
        return start(frames, iterations, numDrones, stepSize, false);
    }

    //same ordeal, now with multithreading enabled :)
    public GDGifOutput startThreads(int threads, int frames, int iterations, int numDrones, double stepSize){
        ThreadHandler threadHandler = new ThreadHandler(threads, frames);
        GDOutput[] outputs = threadHandler.start(iterations, numDrones, stepSize, frames, image);
        return new GDGifOutput(outputs);
    }
}
