

import java.nio.channels.Pipe;
import java.util.ArrayList;

import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;

public class ThreadHandler {

    private int processLimits;
    private int completed;
    private int next;
    private GDOutput[] outputs;
    private int currentRunnning;

    public ThreadHandler(int number){
        processLimits = number;
        completed = 0;
        next = 0;
        outputs = new GDOutput[number];
    }

    public GDOutput[] start(int iterations, int numDrones, double stepSize, int frameCount, Pixels image){
        while(completed < frameCount){
            if(currentRunnning < processLimits){
                new ImageThread(next, image, this).setup(iterations, numDrones, stepSize).start();
                next++;
                currentRunnning++;
            }
        }
        return outputs;

    }

    public synchronized void remove(int id, GDOutput optimal){
        outputs[id] = optimal;
        currentRunnning--;
        completed++;
        System.out.println("Thread: " + id + " is done, total running: " + currentRunnning);
    }


}
