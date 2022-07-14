package com.example.game.websocket;

import com.example.game.model.user.User;
import com.example.game.repository.user.UserRepository;
import com.example.game.security.UserDetailsImpl;
import com.example.game.websocket.redis.RedisPublisher;
import com.example.game.websocket.redis.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;

    private final RedisMessageListenerContainer redisMessageListener;

    private final RedisSubscriber redisSubscriber;

    //ChatRoom 전체 조회
    public List<GameRoom> getAllGameRooms() {
        return gameRoomRepository.findAllByOrderByCreatedAtDesc();
    }

    // ChatRoom 생성
    public String createGameRoom(GameRoomRequestDto requestDto, UserDetailsImpl userDetails) {
        GameRoom room = GameRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(requestDto.getRoomName())
                .build();
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                ()-> new NullPointerException("유저 없음"));
        gameRoomRepository.save(room);
        room.addUser(user);
        user.setRoomId(room.getRoomId());
        System.out.println("gameRoom roomId = " + room.getRoomId()); // roomId 콘솔창에 찍기
        return room.getRoomId();
    }

    // roomName 검색으로 ChatRoom 조회
    public List<GameRoom> searchGameRooms(String keyword) {
        return gameRoomRepository.findByRoomNameContaining(keyword);
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enter() {
        ChannelTopic topic = new ChannelTopic("public");
        if (topic == null) {
            topic = new ChannelTopic("public");
            redisMessageListener.addMessageListener(redisSubscriber, topic);
            topics.put(topic);
        }
    }

    public ChannelTopic getTopic() {
        return topics;
    }
}
//package com.example.game.websocket;
//
//import com.example.game.Game.GameRoom;
//import com.example.game.security.UserDetailsImpl;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class GameRoomService {
//
//    private final GameRoomRepository gameRoomRepository;
//
//    //ChatRoom 전체 조회
//    public List<GameRoom> getAllGameRooms() {
//        return gameRoomRepository.findAllByOrderByCreatedAtDesc();
//    }
//
//    // ChatRoom 생성
//    public ResponseEntity createGameRoom(GameRoomRequestDto requestDto, UserDetailsImpl userDetails) {
//        GameRoom room = GameRoom.builder()
//                .roomId(UUID.randomUUID().toString())
//                .nickName(userDetails.getUser().getNickname())
//                .roomName(requestDto.getRoomName())
//                .build();
//        gameRoomRepository.save(room);
//        System.out.println("ChatRoom roomId = " + room.getRoomId()); // roomId 콘솔창에 찍기
//        return new ResponseEntity("생성 성공", HttpStatus.OK);
//    }
//
//    // roonName 검색으로 ChatRoom 조회
//    public List<GameRoom> searchGameRooms(String keyword) {
//        return gameRoomRepository.findByRoomNameContaining(keyword);
//    }
//}

