package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer>{


    Optional<Booking> findById(Integer bookingId);

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE hotel_id = :hotelId AND room_id = :roomId " +
            "AND booking_status != 3 " +
            "AND (check_in BETWEEN :checkIn AND :checkOut OR check_out BETWEEN :checkIn AND :checkOut)", nativeQuery = true)
    long countByHotelIdAndRoomIdAndCheckInBetween(@Param("hotelId") int hotelId,
                                                  @Param("roomId") int roomId,
                                                  @Param("checkIn") LocalDate checkIn,
                                                  @Param("checkOut") LocalDate checkOut);



}
