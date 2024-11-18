package com.computation;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class ComputationComponent {
    private String name;
    private int idx;
    private boolean isCancelled = false;
    private boolean isFinished = false;
    private final PipedInputStream inputStream = new PipedInputStream();
    private final PipedOutputStream outputStream = new PipedOutputStream();

    public boolean isFinished() {
        return isFinished;
    }

    public String getName() {
        return name;
    }

    public ComputationComponent(String name, int idx) {
        this.name = name;
        this.idx = idx;
        try {
            inputStream.connect(outputStream); /
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // run the computation asynchronously in a new thread
    public void runComputation() {
        new Thread(() -> {
            try {
                if (isCancelled) return;

                System.out.println("Running computation for component " + name);
                Thread.sleep(2000);
                if (isCancelled) return;

                // write a simple byte (1) to the output stream to simulate a result
                outputStream.write(1);
                outputStream.flush();
                System.out.println("Computation finished for " + name);

                // start reading the result from the pipe
                readFromPipe();

                isFinished = true; // Mark computation as finished
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // read the result from the pipe in a separate thread
    public void readFromPipe() {
        new Thread(() -> {
            try {
                // read data from the pipe
                int data = inputStream.read();
                System.out.println("Received data: " + data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void cancelComputation() {
        isCancelled = true;
        System.out.println("Computation cancelled for " + name);
    }

    // print the status of the computation
    public void printStatus() {
        if (isCancelled) {
            System.out.println(name + " was cancelled.");
        } else if (isFinished) {
            System.out.println(name + " computation finished.");
        } else {
            System.out.println(name + " is still running.");
        }
    }
}
