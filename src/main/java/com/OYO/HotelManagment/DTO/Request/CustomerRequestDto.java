package com.OYO.HotelManagment.DTO.Request;

//import com.OYO.HotelManagment.Model.Aadhar;
import com.OYO.HotelManagment.Model.Booking;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;

@Data
public class CustomerRequestDto {

   private Integer id;
   private String name;
   private String contactNumber;
   private String email;
   private String password;
//    Aadhar aadhar;
}
