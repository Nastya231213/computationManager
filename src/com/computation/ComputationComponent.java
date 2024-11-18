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

    public ComputationComponent(String name, int idx) {
        this.name = name;
        this.idx = idx;
        try {
            inputStream.connect(outputStream); // Connect the pipe
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void runComputation() {
        new Thread(() -> {
            try {
                if (isCancelled) return;

                System.out.println("Running computation for component " + name);
                Thread.sleep(2000);
                if (isCancelled) return;

                outputStream.write(1);
                outputStream.flush();
                System.out.println("Computation finished for " + name);

                isFinished = true;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void cancelComputation() {
        isCancelled = true;
        System.out.println("Computation cancelled for " + name);
    }

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