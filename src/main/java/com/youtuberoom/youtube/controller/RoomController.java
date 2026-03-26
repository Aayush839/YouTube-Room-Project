package com.youtuberoom.youtube.controller;

import com.youtuberoom.youtube.model.Room;
import com.youtuberoom.youtube.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/ping") // ✅ ADD THIS
    public String ping() {
        return "OK";
    }
    @PostMapping("/create")
    public Room createRoom(@RequestParam String hostId) {
        return roomService.createRoom(hostId);
    }

    @PostMapping("/join")
    public Map<String, Object> joinRoom(@RequestParam String roomId,@RequestParam String userId) {

        roomService.joinRoom(roomId, userId);

        messagingTemplate.convertAndSend("/topic/room/" + roomId,Map.of("type", "PARTICIPANTS", "data", roomService.getParticipants(roomId)));
        messagingTemplate.convertAndSend("/topic/room/" + roomId, roomService.getRoomState(roomId)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("participants", roomService.getParticipants(roomId));
        response.put("state", roomService.getRoomState(roomId));

        return response;
    }

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }
}