package com.lakesidehotel.hotelbooking.service;

import com.lakesidehotel.hotelbooking.model.BookedRoom;

import java.util.List;

public interface IBookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
