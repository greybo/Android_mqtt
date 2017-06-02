package com.example.android_mqtt.model;

import com.google.gson.annotations.SerializedName;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class Audio {

    @SerializedName("zcr")
    private double zcr;
    @SerializedName("mfcc")
    private byte[] mfcc;

    public Audio() {
        this.zcr = new Random().nextDouble();

        this.mfcc = new byte[14];
        new Random().nextBytes(mfcc);

//        UUID uuid = UUID.randomUUID();
//        long hi = uuid.getMostSignificantBits();
//        long lo = uuid.getLeastSignificantBits();
//        this.mfcc= ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
    }

    @Override
    public String toString() {
        return "Audio{" +
                "zcr=" + zcr +
                ", mfcc=" + Arrays.toString(mfcc) +
                '}';
    }
}
