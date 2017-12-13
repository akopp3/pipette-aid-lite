package com.akoppu.pipetteaid20;

import java.util.List;

/**
 * Created by cngos on 11/14/2017.
 */

public class Semiprotocol {

    private final List<LabTask> steps;
    private String name;

    public Semiprotocol() {
        steps = null;
    }

    public Semiprotocol(List<LabTask> steps, String name) {
        this.steps = steps;
        this.name = name;
    }


    public List<LabTask> getSteps() {
        return steps;
    }

    public String getName(){
        return name;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LabTask t: getSteps()) {
            sb.append(t.toString()).append("\n");
        }
        return sb.toString();
    }

}
