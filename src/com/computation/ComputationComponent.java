package com.computation;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.CompletableFuture;

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
            inputStream.connect(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Thread.sleep(2000); // симуляція роботи
                if (isCancelled) return;

                // Записати результат у потік
                outputStream.write(1);
                outputStream.flush();
                System.out.println("Computation finished for " + name);

                // Читання результату
                readFromPipe();

                isFinished = true;
            } catch (InterruptedException | IOException e) {
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
