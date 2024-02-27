

import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;

// Thread that does gradient descent on one frame of the GIF
public class ImageThread extends Thread{
    private int id;
    private GradientDescent gd;
    private Pixels oldImage;
    private ThreadHandler manager;
    private int iterations;
    private int numDrones;
    private double stepSize;

    public ImageThread(int id, Pixels image, ThreadHandler th){
        this.id =id;
        this.oldImage = image;
        this.manager = th;
    }

    public ImageThread setup(int iterations, int numDrones, double stepSize){
        this.iterations = iterations;
        this.numDrones = numDrones;
        this.stepSize = stepSize;
        return this;
    }

    //creates a new thread that runs gradient descent on a frame (split by the gif)
    @Override
    public void run(){
        //string formatting workaround
        String newIndex = "";
        int num = id;
        if(num < 100){
            newIndex += "0";
        }
        if(num < 10){
            newIndex += "0";
        }
        newIndex += num;
        String newString = "./frames/frame_" + newIndex + "_delay-0.04s.jpg";
        // prep infrastructure
        Pixels newImage = new Pixels(oldImage.width, newString);
        // execute GD
        gd = new GradientDescent(newImage);
        GDOutput output;
        output = gd.start(iterations, numDrones, stepSize);
        // callback to the manager to remove this thread and log the output produced
        manager.remove(id, output);

        this.interrupt();
    }
}
