package com.computation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ComputationManager {
    private static final ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<>();

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
                component.cancelComputation();
            }
        }
    }

    public static void runComputationAsync(ComputationComponent component) {
        CompletableFuture.runAsync(component::runComputation);
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

    public static void summary() {
        groups.forEach((name, group) -> group.printSummary());
    }

    public static void main(String[] args) {
        Group group1 = ComputationManager.newGroup("Group1");
        ComputationComponent comp1 = ComputationManager.addComponentToGroup("Group1", "ComponentA", 1);
        ComputationComponent comp2 = ComputationManager.addComponentToGroup("Group1", "ComponentB", 2);

        runComputationAsync(comp1);
        runComputationAsync(comp2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // cancel one computation
        cancelComputation("Group1", "ComponentA");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        summary();
    }
}
