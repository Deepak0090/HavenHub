package com.OYO.HotelManagment.DTO.Request;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotelRequestDto {

    private  String hotelName;
    private  String Address;
    private String contactNumber;
    private  Boolean Status;
    private LocalDateTime onbordDate;

}
