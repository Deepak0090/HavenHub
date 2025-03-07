package com.OYO.HotelManagment.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotel_room_mappings")
public class HotelRoomMappings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "hotel_id")
    Integer hotelId;

    @Column(name = "room_id")
    Integer roomId;

    @Column(name = "total_rooms")
    Integer totalRooms;


}
