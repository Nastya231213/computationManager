package com.computation;

import com.utils.MathUtils;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CompletableFuture;

public class ComputationComponent {
    private String name;
    private int idx;
    private final String functionType;
    private boolean isCancelled = false;
    private boolean isFinished = false;
    private final PipedInputStream inputStream = new PipedInputStream();
    private final PipedOutputStream outputStream = new PipedOutputStream();
    private double result;
    private double argument;
    public ComputationComponent(String name, int idx, String functionType) {
        this.name = name;
        this.idx = idx;
        this.functionType = functionType;

        try {
            inputStream.connect(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setArgument(double argument) {
        this.argument = argument;
    }
    public String getName() {
        return name;
    }

    public boolean isFinished() {
        return isFinished;
    }
    // run the computation asynchronously in a new thread

    public void runComputation() {
        CompletableFuture.runAsync(() -> {
            try {
                if (isCancelled) return;

                System.out.println("Running computation for component " + name);
                Thread.sleep(2000);
                if (isCancelled) return;

                switch (functionType) {
                    case "factorial" -> result = MathUtils.factorial((int) argument);
                    case "sqrt" -> result = MathUtils.squareRoot(argument);
                    case "power" -> result = MathUtils.power(argument, 2); //
                    default -> throw new IllegalArgumentException("Unsupported function type: " + functionType);
                }
                readFromPipe();

                isFinished = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    // read the result from the pipe in a separate thread

    private void readFromPipe() {
        CompletableFuture.runAsync(() -> {
            try {
                int data = inputStream.read();
                System.out.println("Received data: " + data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
