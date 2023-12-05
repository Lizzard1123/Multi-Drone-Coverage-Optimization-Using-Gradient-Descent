

import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;

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

    @Override
    public void run(){
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
        Pixels newImage = new Pixels(oldImage.width, newString);
        gd = new GradientDescent(newImage);

        GDOutput output;
        output = gd.start(iterations, numDrones, stepSize);
        
        manager.remove(id, output);
    }
}
