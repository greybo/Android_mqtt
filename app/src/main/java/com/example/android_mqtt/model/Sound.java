package com.example.android_mqtt.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Sound {

    @SerializedName("data")
    private List<Audio> data;

    public Sound() {
        data = new ArrayList<>();
        for (int i = 0; i < 98; i++) {
            Audio audio = new Audio();
            data.add(audio);
        }
    }

    @Override
    public String toString() {
        return "Sound{" +
                "data=" + data +
                '}';
    }
}
