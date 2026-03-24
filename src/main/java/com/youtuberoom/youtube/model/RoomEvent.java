package com.youtuberoom.youtube.model;


import lombok.Data;

@Data
public class RoomEvent {
    private String type;
    private String roomId;
    private String videoId;
    private double timestamp;
    private String userId;
    private String targetUserId;
    private String hostId;
    private String role;
    private String eliminateIds;
}