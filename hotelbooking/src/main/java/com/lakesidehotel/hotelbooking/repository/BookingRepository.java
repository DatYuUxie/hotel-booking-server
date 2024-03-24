package com.lakesidehotel.hotelbooking.repository;

import com.lakesidehotel.hotelbooking.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> findAll();
}
