package com.youtuberoom.youtube.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Room {
    private String roomId;
    private String hostId;
    private Map<String, String> participants = new HashMap<>();
    private String currentVideoId;
    private double currentTime;
    private boolean playing;
}
