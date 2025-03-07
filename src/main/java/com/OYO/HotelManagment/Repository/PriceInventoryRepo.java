package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.PriceInventoryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceInventoryRepo extends JpaRepository<PriceInventoryDetails, Integer> {

//    List<PriceInventoryDetails> findByHotelIdAndCheckIn(Integer hotelId, LocalDate checkIn, LocalDate checkOut);

    Optional<PriceInventoryDetails> findByHotelIdAndRoomIdAndDate(Integer hotelId, Integer roomId, LocalDate date);
    List<PriceInventoryDetails> findByHotelIdAndDate(Integer hotelId, LocalDate date);
    List<PriceInventoryDetails> findByHotelIdAndDateBetween(Integer hotelID, LocalDate startDate, LocalDate endDate);

}
