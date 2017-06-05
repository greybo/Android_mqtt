package com.example.android_mqtt.util;


import java.util.LinkedList;

public class DetectorThread extends Thread {

    private volatile Thread thread;

    private LinkedList<Boolean> clapResultList = new LinkedList<Boolean>();
    private int numClaps;
    private int totalClapsDetected = 0;
    private int clapCheckLength = 3;
    private int clapPassScore = 3;

    public DetectorThread() {

    }

    private void initBuffer() {
        numClaps = 0;
        clapResultList.clear();

        // init the first frames
        for (int i = 0; i < clapCheckLength; i++) {
            clapResultList.add(false);
        }
        // end init the first frames
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stopDetection() {
        thread = null;
    }

    public void run() {

    }

    public int getTotalClapsDetected() {
        return totalClapsDetected;
    }
}


