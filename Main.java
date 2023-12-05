import helpers.Coords;
import helpers.GDGifOutput;
import helpers.GDOutput;
import helpers.Pixels;

public class Main {
    public static void main(String[] args){
        int width = 100;
        int numDrones = 15;
        int radius = 10;
        int frames = 256;
        String name = "./frames/frame_000_delay-0.04s.jpg";
        Coords.setRadius(radius);
        Pixels image = new Pixels(width, name);
        image.listInfo();

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

        GradientDescent gd = new GradientDescent(image);
        double stepSize = 5;
        int iterations = 100;
        int processes = 16;
        //GDOutput output = gd.start(iterations, numDrones, stepSize);
        // GDGifOutput output = gd.start(frames, iterations, numDrones, stepSize);
        GDGifOutput output = gd.startThreads(processes, frames, iterations, numDrones, stepSize);
        output.createFile(stepSize, numDrones, Coords.radius, width, image.getSize());

    }
}