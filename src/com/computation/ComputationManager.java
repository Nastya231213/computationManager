package com.computation;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

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

    // сancel a specific computation component
    public static void cancelComputation(String groupName, String componentName) {
        Group group = groups.get(groupName);
        if (group != null) {
            ComputationComponent component = group.getComponent(componentName);
            if (component != null) {
                component.cancelComputation(); //
            }
        }
    }

    public static void runComputationAsync(ComputationComponent component) {
        executorService.submit(() -> {
            component.runComputation();
            try {
                while (!component.isFinished()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
//cancel a group
    public static void cancelAllComputationsInGroup(String groupName) {
        Group group = groups.get(groupName);
        if (group != null) {
            group.cancelGroup();
        }
    }

    public static void cancelAllComputations() {
        groups.forEach((name, group) -> group.cancelGroup());
    }

    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public static void summary() {
        groups.forEach((name, group) -> group.printSummary());
    }

    public static void main(String[] args) {
        Group group1 = ComputationManager.newGroup("Group1");
        ComputationComponent comp1 = ComputationManager.addComponentToGroup("Group1", "ComponentA", 1);
        ComputationComponent comp2 = ComputationManager.addComponentToGroup("Group1", "ComponentB", 2);

        Group group2 = ComputationManager.newGroup("Group2");
        ComputationComponent comp4 = ComputationManager.addComponentToGroup("Group2", "ComponentD", 1);
        ComputationComponent comp5 = ComputationManager.addComponentToGroup("Group2", "ComponentE", 2);

        runComputationAsync(comp1);
        runComputationAsync(comp2);
        runComputationAsync(comp4);
        runComputationAsync(comp5);

        try {
            Thread.sleep(2000); // wait for some time before cancelling
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cancel one computation
        cancelComputation("Group1", "ComponentA");

        try {
            Thread.sleep(5000); // wait for computations to finish or be cancelled
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shutdown();

        // print summary
        summary();
    }
}
