package com.akoppu.pipetteaid20;

/**
 * Created by cngos on 12/1/2017.
 */

public class Inventory {

    private int P1000, P200, P20, pcr_plate_96, pcr_strip, pcr_tube;

    public Inventory() {

    }

    public Inventory(int P1000, int P200, int P20, int pcr_plate_96, int pcr_strip, int pcr_tube) {
        this.P1000 = P1000;
        this.P20 = P20;
        this.P200 = P200;
        this.pcr_plate_96 = pcr_plate_96;
        this.pcr_strip = pcr_strip;
        this.pcr_tube = pcr_tube;
    }

    public int getP20() {
        return P20;
    }

    public int getP200() {
        return P200;
    }

    public int getP1000() {
        return P1000;
    }

    public int getPcr_plate_96() {
        return pcr_plate_96;
    }

    public int getPcr_strip() {
        return pcr_strip;
    }

    public int getPcr_tube() {
        return pcr_tube;
    }
}
