package com.OYO.HotelManagment.DTO.Response;


import com.OYO.HotelManagment.Enum.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto{

   private String errorMessage;

   private LocalDate checkOut;

   private BookingStatus bookingStatus;

   private Integer hotelId;

   private Integer roomId;

   private Integer bookingAmount;

   private LocalDate checkIn;

   private Boolean isPrepaid;

   private Integer numberOfGuest;
}
