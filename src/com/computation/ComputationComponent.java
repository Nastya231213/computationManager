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
            // Connect the input and output streams to establish communication via pipe
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

    // Run the computation asynchronously in a new thread
    public void runComputation() {
        CompletableFuture.runAsync(() -> {
            try {
                if (isCancelled) return;

                System.out.println("Running computation for component " + name);
                Thread.sleep(2000);

                if (isCancelled) return;

                switch (functionType) {
                    case "factorial":
                        result = MathUtils.factorial((int) argument);
                        break;
                    case "sqrt":
                        result = MathUtils.squareRoot(argument);
                        break;
                    case "power":
                        result = MathUtils.power(argument, 2);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported function type: " + functionType);
                }

                // Send the result through the pipe to another component or thread
                sendResultThroughPipe();

                isFinished = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                closePipe();
            }
        });
    }
    private void closePipe() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResultThroughPipe() {
        try {
            // Send the result as a string through the output stream
            String resultString = String.valueOf(result);
            outputStream.write(resultString.getBytes());
            outputStream.flush();
            System.out.println("Sent result through pipe: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Read from the pipe in a separate thread
    public void readFromPipe() {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead != -1) {
                    String resultString = new String(buffer, 0, bytesRead);
                    System.out.println("Received data from pipe: " + resultString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

