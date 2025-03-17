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

   private Integer bookingId;

   private Integer hotelId;

   private Integer roomId;

   private Integer customerId;

   private LocalDate checkIn;

   private LocalDate checkOut;

   private Double bookingAmount;

//   private BookingStatus bookingStatus;

//   private Boolean isPrepaid;

   private String message;
}
