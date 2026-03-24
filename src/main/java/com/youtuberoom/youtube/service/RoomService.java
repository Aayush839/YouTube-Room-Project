package com.youtuberoom.youtube.service;

import com.youtuberoom.youtube.model.Room;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RoomService {

    private Map<String, Room> rooms = new HashMap<>();

    public Room createRoom(String hostId) {
        Room room = new Room();
        String roomId = UUID.randomUUID().toString();

        room.setRoomId(roomId);
        room.setHostId(hostId);
        room.getParticipants().put(hostId, "HOST");

        rooms.put(roomId, room);
        return room;
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void addParticipant(String roomId, String userId) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.getParticipants().put(userId, "PARTICIPANT");
        }
    }
    public void joinRoom(String roomId, String userId) {
        Room room = rooms.get(roomId.trim());
        if (room != null) {
            if(room.getParticipants().containsKey(userId))  return;
            if(userId.equals(room.getHostId()))       room.getParticipants().put(userId, "HOST");
            else  room.getParticipants().put(userId, "PARTICIPANT");
        } else {
            throw new RuntimeException("Room not found");
        }
    }
    public String getUserRole(String roomId, String userId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        return room.getParticipants().get(userId);
    }

    public void updatePlayState(String roomId, boolean playing) {
        rooms.get(roomId).setPlaying(playing);
    }

    public void updateTime(String roomId, double time) {
        rooms.get(roomId).setCurrentTime(time);
    }

    public void updateVideo(String roomId, String videoId) {
        rooms.get(roomId).setCurrentVideoId(videoId);
    }
    public void assignRole(String roomId,String hostId,String  targetUserId,String newRole){
        Room room=rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        if(!"HOST".equals(room.getParticipants().get(hostId))){
            throw new RuntimeException("Only Host can assign roles");
        }
        room.getParticipants().put(targetUserId,newRole);
    }

    public void removeParticipant(String roomId, String hostId, String targetUserId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        String role  = room.getParticipants().get(hostId);

        if (!"HOST".equals(role)) {
            throw new RuntimeException("Only host can remove users");
        }
        room.getParticipants().remove(targetUserId);
    }
    public Map<String, String> getParticipants(String roomId) {
        return rooms.get(roomId).getParticipants();
    }

    public Map<String, Object> getRoomState(String roomId) {

        Room room = rooms.get(roomId);
        Map<String, Object> state = new HashMap<>();
        state.put("type", "SYNC_STATE");
        state.put("currentVideoId", room.getCurrentVideoId());
        state.put("currentTime", room.getCurrentTime());
        state.put("playing", room.isPlaying());
        return state;
    }

    public void leaveRoomUser(String roomId,String userId){
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found");
        }
        room.getParticipants().remove(userId);
    }
}