package com.OYO.HotelManagment.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "price_inventory_details")
public class PriceInventoryDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "hotel_id")
    Integer hotelId;

    @Column(name = "room_id")
    Integer roomId;

    @Column(name = "available_rooms")
    Integer availableRooms;

    @Column(name = "price")
    Integer price;


   @Column(name = "date" )
    LocalDate date;
}
