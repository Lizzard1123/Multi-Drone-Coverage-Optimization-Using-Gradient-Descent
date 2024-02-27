import helpers.Coords;
import helpers.GDGifOutput;
import helpers.GDOutput;
import helpers.Pixels;

public class Main {
    public static void main(String[] args){
        // desired width (in pixels) of the probability matrix (can be the full size of the input image, or less)
        // the smaller this matrix is, the faster the computations
        int width = 100;
        // the number of drones to run in the simulation
        int numDrones = 15;
        // the coverage radius (search radius) of a drone (relative to the size of the probability matrix)
        int radius = 10;
        // how long the gif is in # of frames, each frame will run with Gradient Descent
        int frames = 256;
        // file path of the desired burn probability matrix (will be shrunk down by width variable)
        String name = "./BPmap2.jpeg";
        Coords.setRadius(radius);
        Pixels image = new Pixels(width, name);
        image.listInfo();


        /*
         *
         * OLD scripts built up while testing
         * wont remove in the case that one of them is useful
         * not guaranteed to work
         *
         */

        //image.printImage(image.getGrid(20));
        //image.printImage();

        // Coords[] randomLocations = image.randomPositions(10);
        // image.printLocations(randomLocations);
        // System.out.println("------Results------");
        // double coverage = image.coverage(randomLocations);
        // System.out.println(coverage);

        //monte carlo

        // int simulations = 100000; // 1 million
        // double bestCoverage = 0;
        // Coords[] bestMatrix = new Coords[numDrones];


        // for(int i = 0; i < simulations; i++){
        //     Coords[] randomLocations = image.randomPositions(numDrones);
        //     double coverage = image.coverage(randomLocations);
        //     if(coverage > bestCoverage){
        //         bestCoverage = coverage;
        //         bestMatrix = randomLocations;
        //     }
        //     //10% updates
        //     if(i % (simulations / 10) == 0){
        //         System.out.println("--------------------------");
        //         System.out.println("At " + i + " of " + simulations);
        //         System.out.println("Current Best: " + bestCoverage);
        //     }
        // }
        // System.out.println("--------------------------");
        // System.out.println("Done");
        // System.out.println("Best: " + bestCoverage);
        // image.printLocations(bestMatrix);

        /*
         * [17, 17],
            [2, 17],
            [7, 17],
            [17, 7],
            [4, 13],
            [12, 10],
            [2, 8],
            [18, 12],
            [12, 16],
            [17, 2],
         */
        //test my own
        // Coords[] myCoords = new Coords[10];
        // myCoords[0] = new Coords(17, 17);
        // myCoords[1] = new Coords(2, 17);
        // myCoords[2] = new Coords(7, 17);
        // myCoords[3] = new Coords(17, 7);
        // myCoords[4] = new Coords(4, 3);
        // myCoords[5] = new Coords(12, 10);
        // myCoords[6] = new Coords(2, 8);
        // myCoords[7] = new Coords(18, 12);
        // myCoords[8] = new Coords(12, 16);
        // myCoords[9] = new Coords(17, 2);
        // System.out.println(image.coverage(myCoords));

        // test GD derivatives
        // Coords[] randomLocations = image.randomPositions(numDrones);
        // GradientDescent gd = new GradientDescent(image);
        // gd.printDerivatives(gd.getAllGradients(randomLocations));

        //GDOutput output = gd.start(iterations, numDrones, stepSize);

        /*
         *
         * Start of code
         *
         */

        GradientDescent gd = new GradientDescent(image);
        // step size of the Gradient Descent
        double stepSize = 5;
        // # of iterations of Gradient Descent, I tried using a convergence threshold, but it would prematurely end before the heuristics kicked in
        int iterations = 100;
        // used in the case of multithreading
        int processes = 16;

        /*
         * Start commands
         */

        // single threaded approach
        // has to be used in order to warm start GD from the results of the previous frame, takes around 8 min
        // change the last parameter here to false to disable warm starting each frame (will be random every start)
        //GDGifOutput output = gd.start(frames, iterations, numDrones, stepSize, true);

        // multithreaded approach
        // fast, no warm start, higher variance in results between frames, less likely to get stuck in local minimum and thus higher average
        // live updates in the terminal
        GDGifOutput output = gd.startThreads(processes, frames, iterations, numDrones, stepSize);

        // creates the output.js file in the website directory, which is the JSON data dump
        output.createFile(stepSize, numDrones, Coords.radius, width, image.getSize());

    }
}