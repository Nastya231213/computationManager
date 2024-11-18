package com.computation;

import com.computation.ComputationComponent;
import com.computation.Group;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class ComputationManager {

    private static final ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static Group newGroup(String groupName) {
        Group group = new Group(groupName);
        groups.put(groupName, group);
        return group;
    }

    public static ComputationComponent addComponentToGroup(String groupName, String componentName, int idx) {
        Group group = groups.get(groupName);
        if (group != null) {
            ComputationComponent component = new ComputationComponent(componentName, idx);
            group.addComponent(component);
            return component;
        }
        return null;
    }

    public static void cancelComputation(String groupName, String componentName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.cancelComponent(componentName);
        }
    }

    // Summary after computations are completed
    public static void summary() {
        groups.forEach((name, group) -> group.printSummary());
    }

    // Main method to run the computation manager
    public static void main(String[] args) throws InterruptedException {
        Group group1 = ComputationManager.newGroup("Group1");
        ComputationComponent comp1 = ComputationManager.addComponentToGroup("Group1", "ComponentA", 1);
        ComputationComponent comp2 = ComputationManager.addComponentToGroup("Group1", "ComponentB", 2);

        Group group2 = ComputationManager.newGroup("Group2");
        ComputationComponent comp4 = ComputationManager.addComponentToGroup("Group2", "ComponentD", 1);
        ComputationComponent comp5 = ComputationManager.addComponentToGroup("Group2", "ComponentE", 2);

        // Run components asynchronously
        runComputationAsync(comp1);
        runComputationAsync(comp2);
        runComputationAsync(comp4);
        runComputationAsync(comp5);

        // Simulating cancellation
        Thread.sleep(1000);
        ComputationManager.cancelComputation("Group1", "ComponentB");

        // Summary
        ComputationManager.summary();

        // Shutting down the executor service
        executorService.shutdown();
    }

    // Helper method to run computation asynchronously
    private static void runComputationAsync(ComputationComponent component) {
        executorService.submit(() -> {
            component.runComputation();
        });
    }
}
