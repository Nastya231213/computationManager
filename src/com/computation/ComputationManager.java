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
    public static ComputationComponent addComponentToGroup(String groupName, String componentName, int idx, String functionType) {
        Group group = groups.get(groupName);
        if (group != null) {
            ComputationComponent component = new ComputationComponent(componentName, idx, functionType);
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
public static void runGroup(String groupName) {
    Group group = groups.get(groupName);
    if (group != null) {
        group.getComponents().forEach((name, component) ->
                CompletableFuture.runAsync(component::runComputation)
        );
    }
}

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
        ComputationComponent comp1 = ComputationManager.addComponentToGroup("Group1", "ComponentA", 1, "factorial");
        ComputationComponent comp2 = ComputationManager.addComponentToGroup("Group1", "ComponentB", 2, "sqrt");
        group1.setSharedArgument(5);
        Group group2 = ComputationManager.newGroup("Group2");
        ComputationComponent comp3 = ComputationManager.addComponentToGroup("Group2", "ComponentC", 1, "power");
        ComputationComponent comp4 = ComputationManager.addComponentToGroup("Group2", "ComponentD", 2, "factorial");
        group1.setSharedArgument(5);
        runGroup("Group1");
        runGroup("Group2");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // cancel one computation
        cancelComputation("Group1", "ComponentA");
        comp3.readFromPipe();
        comp2.readFromPipe();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        summary();
    }
}
