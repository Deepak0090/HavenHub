package com.OYO.HotelManagment.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "contact_Number")
    String contactNumber;


    @Column(name = "email_id", unique = true)
    String email;

    @Column(name = "status")
    String status;

    @Column(name = "date&Time")
    LocalDateTime createdAt;


//    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
//    Aadhar aadhar;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    List<Booking> bookings = new ArrayList<>();

}
