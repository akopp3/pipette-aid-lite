package com.akoppu.pipetteaid20;

/**
 * Created by cngos on 11/14/2017.
 */

public class LabTask {

    private String operation;
    private String name, type, source, dest;
    private boolean isNew;
    double vol;

    public LabTask() {

    }

    public LabTask(String operation, String name, String type, String source, String dest, double vol, boolean isNew) {
        this.operation = operation;
        this.name = name;
        this.type = type;
        this.source = source;
        this.dest = dest;
        this.vol = vol;
        this.isNew = isNew;
    }

    public String getOperation() { return operation; }

    public String getSource() { return source; }

    public String getDest() {return dest; }

    public boolean getisNew() {return isNew; }

    public String getType() { return type; }

    public double getVol() {return vol;}

    public String getName() {return name;}

}
