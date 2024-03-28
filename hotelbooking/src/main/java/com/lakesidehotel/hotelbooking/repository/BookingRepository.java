package com.lakesidehotel.hotelbooking.repository;

import com.lakesidehotel.hotelbooking.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {
    Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoom> findAll();

    List<BookedRoom> findByGuestEmail(String email);
}
