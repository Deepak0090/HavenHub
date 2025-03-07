package com.OYO.HotelManagment.DTO.Response;

import com.OYO.HotelManagment.Model.Booking;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PriceInventoryResponseDto {


   private Integer hotelId;

   private Integer roomId;

   private Boolean isSoldOut;

   private Integer price;

   private LocalDate date;


}
