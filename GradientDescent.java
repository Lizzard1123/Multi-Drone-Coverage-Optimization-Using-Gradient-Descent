import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;
import helpers.PreciseCoords;

public class GradientDescent {
    private Pixels image;

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

    public GDOutput start(int iterations, int numDrones, double stepSize){
        //start in random locations
        Coords[][] history = new Coords[iterations][numDrones];
        Coords[] locations = image.randomPositions(numDrones);
        double[] coverageOverTime = new double[iterations];
        for(int i = 0; i < iterations; i++){
            //get Gradient matrix of coords
            PreciseCoords[] gradient = getAllGradients(locations);
            //update locations
            for(int j = 0; j < numDrones; j++){
                int newX = (int) Math.round(locations[j].getX() + (stepSize * gradient[j].getX()));
                locations[j].setX(newX);
                int newY = (int) Math.round(locations[j].getY() + (stepSize * gradient[j].getY()));
                locations[j].setY(newY);
            }
            // check new positions
            coverageOverTime[i] = image.coverage(locations);
            //copy into history
            for(int k = 0; k < locations.length; k++){
                history[i][k] = new Coords(locations[k].getX(), locations[k].getY());
            }
        }
        return new GDOutput(history, coverageOverTime);
    }
}
