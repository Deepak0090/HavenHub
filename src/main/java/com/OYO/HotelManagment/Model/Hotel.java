package com.OYO.HotelManagment.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "hotel_name", nullable = false)
    String hotelName;

    @Column(name = "hotel_location")
    String address;

    @Column(name = "Contact_Number")
    String contactNumber;

    @Column(name = "date_&_time")
    LocalDateTime onbordDate;

    @Column(name = "isActive")
    Boolean status;


    @JoinTable(name = "hotel_room_mappings",
    joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    @ManyToMany
    List<Room> roomList = new ArrayList<>();
}
