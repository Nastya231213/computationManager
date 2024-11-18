package com.computation;

import com.computation.ComputationComponent;

import java.util.concurrent.ConcurrentHashMap;

public class Group {
    private String name;
    private final ConcurrentHashMap<String, ComputationComponent> components = new ConcurrentHashMap<>();

    public Group(String name) {
        this.name = name;
    }

    public void addComponent(ComputationComponent component) {
        components.put(component.getName(), component);
    }

    public void cancelComponent(String componentName) {
        ComputationComponent component = components.get(componentName);
        if (component != null) {
            component.cancelComputation();
        }
    }

    public void printSummary() {
        System.out.println("Summary for " + name + ":");
        components.forEach((name, component) -> component.printStatus());
    }
}