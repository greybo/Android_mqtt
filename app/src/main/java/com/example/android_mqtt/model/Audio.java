package com.example.android_mqtt.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Random;

public class Audio {

    @SerializedName("zcr")
    private double zcr;
    @SerializedName("mfcc")
    private byte[] mfcc;

    public Audio() {
        this.zcr = new Random().nextDouble();

        this.mfcc = new byte[14];
        new Random().nextBytes(mfcc);
    }

    @Override
    public String toString() {
        return "Audio{" +
                "zcr=" + zcr +
                ", mfcc=" + Arrays.toString(mfcc) +
                '}';
    }
}
