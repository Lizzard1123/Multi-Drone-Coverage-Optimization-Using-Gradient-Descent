

import java.nio.channels.Pipe;
import java.util.ArrayList;

import helpers.Coords;
import helpers.GDOutput;
import helpers.Pixels;

//manages java's threads
public class ThreadHandler {
    private int processLimits;
    private int completed;
    private volatile int next;
    private GDOutput[] outputs;
    private volatile int currentRunnning;

    public ThreadHandler(int number, int frames){
        processLimits = number;
        completed = 0;
        next = 0;
        outputs = new GDOutput[frames];
    }

    //create up to the limit # of threads to process gif
    public GDOutput[] start(int iterations, int numDrones, double stepSize, int frameCount, Pixels image){
        while(completed < frameCount){
            if(currentRunnning < processLimits){
                if(next == frameCount){
                    continue;
                }
                new ImageThread(next, image, this).setup(iterations, numDrones, stepSize).start();
                System.out.println("Starting new thread: " + next);
                next++;
                currentRunnning++;
            }
        }
        System.out.println("ended");
        return outputs;

    }

    //remove thread and log output
    public synchronized void remove(int id, GDOutput optimal){
        outputs[id] = optimal;
        completed++;
        currentRunnning--;
        System.out.println("Thread: " + id + " is done, total running: " + currentRunnning);
    }


}
