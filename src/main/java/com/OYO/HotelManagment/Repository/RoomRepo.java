package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepo extends JpaRepository<Room, Integer> {
         Optional<Room> findById(Integer Id);
}
