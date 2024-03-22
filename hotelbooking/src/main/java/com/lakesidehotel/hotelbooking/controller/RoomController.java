package com.lakesidehotel.hotelbooking.controller;

import com.lakesidehotel.hotelbooking.exception.PhotoRetrievalException;
import com.lakesidehotel.hotelbooking.model.BookedRoom;
import com.lakesidehotel.hotelbooking.model.Room;
import com.lakesidehotel.hotelbooking.response.BookingResponse;
import com.lakesidehotel.hotelbooking.response.RoomResponse;
import com.lakesidehotel.hotelbooking.service.IBookingService;
import com.lakesidehotel.hotelbooking.service.IRoomService;
import com.lakesidehotel.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final IRoomService roomService;
    private final IBookingService bookingService;

    @PostMapping("/add/new-room")
    // http://localhost:9192/rooms/add/new-room
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(),
                savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/room/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }


    @GetMapping()
    public ResponseEntity<List<RoomResponse>> getAllRoom() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms
        ) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }

        }
        return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = getAllBookingByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking -> new BookingResponse(booking.getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode())).toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException exception) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(), photoBytes, bookingInfo);
    }

    private List<BookedRoom> getAllBookingByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }
}
