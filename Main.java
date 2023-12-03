import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;

public class Main {
    public static void main(String[] args){
        int width = 100;
        int numDrones = 15;
        int radius = 10;
        String name = "./frames/frame_100_delay-0.04s.jpg";
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
        GDOutput output = gd.start(iterations, numDrones, stepSize);
        // output.printHistory();
        // output.printIndividualHistory();
        // output.printChanges();
        output.createFile(stepSize, numDrones, Coords.radius, width, image.getSize(), name);

        // double[][] small = {
        //     {0.8828125,0.99609375,0.99609375,0.99609375,0.99609375,0.99609375,0.99609375,0.99609375,0.99609375,0.99609375},
        //     {0.99609375,0.5989583333333334,0.4153645833333333,0.4153645833333333,0.4153645833333333,0.4153645833333333,0.4270833333333333,0.6028645833333334,0.7578125,0.75390625},
        //     {0.99609375,0.4505208333333333,0.4153645833333333,0.4153645833333333,0.4153645833333333,0.4466145833333333,0.5091145833333334,0.921875,0.984375,0.94921875},
        //     {0.99609375,0.3841145833333333,0.3880208333333333,0.3880208333333333,0.3359375,0.5325520833333334,0.9765625,0.99609375,0.99609375,0.99609375},
        //     {0.99609375,0.4114583333333333,0.4231770833333333,0.4153645833333333,0.4114583333333333,0.6640625,0.99609375,0.99609375,0.99609375,0.85546875},
        //     {0.99609375,0.6809895833333334,0.5091145833333334,0.5546875,0.375,0.4661458333333333,0.99609375,0.99609375,0.99609375,0.60546875},
        //     {0.99609375,0.87890625,0.875,0.89453125,0.70703125,0.66015625,0.94140625,0.95703125,0.99609375,0.5052083333333334},
        //     {0.99609375,0.89453125,0.85546875,0.9296875,0.90625,0.796875,0.84765625,0.90234375,0.98046875,0.3697916666666667},
        //     {0.99609375,0.8984375,0.9296875,0.94921875,0.92578125,0.94921875,0.96875,0.90625,0.2955729166666667,0.2799479166666667},
        //     {0.99609375,0.83984375,0.9140625,0.8984375,0.8984375,0.890625,0.89453125,0.84765625,0.2799479166666667,0.2799479166666667}
        // };

        // int x = 7;
        // int y = 4;
        // int radius = 2;
        // ArrayList<Double> values = new ArrayList<>();
        // ArrayList<Double> totals = new ArrayList<>();
        // for(int i = 0; i < small.length; i++){
        //     for(int j = 0; j < small[0].length; j++){
        //         double val = Math.pow(i - x, 2) + Math.pow(j - y, 2);
        //         if(val <= Math.pow(radius, 2)){
        //             values.add(1 - small[i][j]);
        //         }
        //         totals.add(1 - small[i][j]);
        //     }
        // }
        // double totalVal = 0;
        // for(double elm : totals){
        //     totalVal += elm;
        // }
        // double thisVal = 0;
        // for(double elm : values){
        //     thisVal += elm;
        // }
        // double coverage = thisVal / totalVal;
        // System.out.println(coverage * 100);
    }
}