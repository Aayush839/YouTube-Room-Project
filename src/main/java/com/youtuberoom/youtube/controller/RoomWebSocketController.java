package com.youtuberoom.youtube.controller;

import com.youtuberoom.youtube.model.RoomEvent;
import com.youtuberoom.youtube.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;
@Controller
public class RoomWebSocketController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/room/{roomId}")
    public void handleEvent(@DestinationVariable String roomId, @Payload RoomEvent event) {

        switch (event.getType()) {

            case "PLAY":
                roomService.updatePlayState(roomId, true);
                roomService.updateTime(roomId,event.getTimestamp());
                roomService.updateVideo(roomId,event.getVideoId());
                break;

            case "PAUSE":
                roomService.updatePlayState(roomId, false);
                roomService.updateTime(roomId,event.getTimestamp());
                break;

            case "SEEK":
                roomService.updateTime(roomId, event.getTimestamp());
                break;

            case "CHANGE_VIDEO":
                roomService.updateVideo(roomId, event.getVideoId());
                break;

            case "REMOVE_USER":
                roomService.removeParticipant(roomId,event.getHostId(),event.getTargetUserId());
                messagingTemplate.convertAndSend("/topic/room/" + roomId,Map.of("type", "PARTICIPANTS","data", roomService.getParticipants(roomId)));
                messagingTemplate.convertAndSend("/topic/kick/" + event.getTargetUserId(),Map.of("type", "KICKED"));
                return;

            case "ASSIGN_ROLE":
                roomService.assignRole(roomId,event.getHostId(),event.getTargetUserId(),event.getRole());
                messagingTemplate.convertAndSend("/topic/room/" + roomId,Map.of("type", "PARTICIPANTS", "data", roomService.getParticipants(roomId)));
                return;

            case "LEAVE":
                roomService.leaveRoomUser(roomId, event.getUserId());
                messagingTemplate.convertAndSend("/topic/kick/" + event.getTargetUserId(),Map.of("type", "KICKED"));
                messagingTemplate.convertAndSend("/topic/room/" + roomId,Map.of("type", "PARTICIPANTS","data", roomService.getParticipants(roomId)));
                return;
        }
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }
}