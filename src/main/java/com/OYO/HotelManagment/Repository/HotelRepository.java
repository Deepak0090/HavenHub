package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Optional<Hotel> findById(Integer Id);
    List<Hotel> findByAddressContainingIgnoreCase(String address);

}
