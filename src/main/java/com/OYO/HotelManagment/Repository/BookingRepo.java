package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer>{

    Optional<Booking> findById(Integer bookingId);
}
