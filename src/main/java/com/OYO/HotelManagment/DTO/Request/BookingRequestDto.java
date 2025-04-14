package com.OYO.HotelManagment.DTO.Request;

import com.OYO.HotelManagment.Model.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingRequestDto {


   private Integer hotelId;

   private Integer bookingId;

   private Integer roomId;

   private Integer customerID;

   private Integer bookingAmount;

   private LocalDate checkIn;

   private LocalDate checkOut;

   private Boolean isPrepaid;

   private Integer numberOfGuest;

   public BookingRequestDto() {

   }
}
