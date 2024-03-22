package com.lakesidehotel.hotelbooking.service;


import com.lakesidehotel.hotelbooking.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService{
    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return null;
    }
}
