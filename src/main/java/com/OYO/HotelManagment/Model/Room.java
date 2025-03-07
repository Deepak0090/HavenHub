package com.OYO.HotelManagment.Model;

import com.OYO.HotelManagment.Enum.Roomtype;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "amenities")
    String amenities;

    @Column(name = "max_occupancy")
    Integer maxOccupancy;

    @Column(name = "active")
    Boolean isActive;

    @Enumerated
    @Column(name = "room_type")
    Roomtype roomtype;


    @ManyToMany(mappedBy = "roomList")
    @JsonIgnore
    List<Hotel> hotelList = new ArrayList<>();

}
