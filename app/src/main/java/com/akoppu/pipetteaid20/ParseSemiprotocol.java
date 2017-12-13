package com.akoppu.pipetteaid20;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cngos on 11/16/2017.
 */

public class ParseSemiprotocol {
    public void initiate() throws Exception {}

    public Semiprotocol run(String text) throws Exception {
        text = text.replaceAll("\"", "");
        String[] lines = text.split("\\r|\\r?\\n");
        List<LabTask> steps = new ArrayList<>();

        //Process each good line
        for (int i = 0; i < lines.length; i++) {
            String aline = lines[i];

            //Ignore blank lines
            if (aline.trim().isEmpty()) {
                continue;
            }

            //Ignore commented out lines
            if (aline.trim().startsWith("//")) {
                continue;
            }

            String[] tabs = aline.split("\t");
            LabOp operation = LabOp.valueOf(tabs[0]);

            steps.add(createTask(tabs, operation));
        }

        return new Semiprotocol(steps, "alibaba_steps");
    }

    private LabTask createAddContainer(String[] tabs, String op) {
        //addContainer	pcr_plate_96	alibaba_oligos	deck/A1
        String tubeType = tabs[1];
        String name = tabs[2];
        String location = tabs[3];
        boolean isnew = Boolean.parseBoolean(tabs[4]);
        return new LabTask(op, name, tubeType, location, null, 0.0, isnew);
    }

    private LabTask createRemoveContainer(String[] tabs, String op) {
        //return new RemoveContainer(tabs[1]);
        return null;
    }

    private LabTask createTransfer(String[] tabs, String op) {
        String source = tabs[1];
        String dest = tabs[2];
        double volume = Double.parseDouble(tabs[3]);
        return new LabTask(op, "transfer", "transfer", source, dest, volume, true);
    }

    private LabTask createDispense(String[] tabs, String op) {
        String reagent = tabs[1];
        String dstContainer = tabs[2];
        double volume = Double.parseDouble(tabs[3]);
        return new LabTask(op, "dispense", reagent,  "source", dstContainer,  volume, true);
    }

    private LabTask createMultichannel(String[] tabs, String op) {
//        String sourceStart = tabs[1];
//        String sourceEnd = tabs[2];
//        String destStart = tabs[3];
//        String destEnd = tabs[4];
//        double volume = Double.parseDouble(tabs[5]);
//        return new Multichannel( sourceStart,  sourceEnd,  destStart,  destEnd,  volume);
        return null;
    }

    private LabTask createTask(String[] tabs, LabOp operation) {
        switch (operation) {
            case addContainer:
                return createAddContainer(tabs, "addContainer");
            case removeContainer:
                return createRemoveContainer(tabs, "removeContainer");
            case transfer:
                return createTransfer(tabs, "transfer");
            case dispense:
                return createDispense(tabs, "dispense");
            case multichannel:
                return createMultichannel(tabs, "multichannel");
        }

        throw new RuntimeException("Operation requested that cannot be parsed " + operation);
    }

//    public static void main(String[] args) throws Exception {
//        String text = readResourceFile("alibaba_semiprotocol.txt");
//        ParseSemiprotocol parser = new ParseSemiprotocol();
//        parser.initiate();
//        Semiprotocol protocol = parser.run(text);
//        System.out.println("# steps: " + protocol.getSteps().size());  //protocol has 10 steps
//    }

}
