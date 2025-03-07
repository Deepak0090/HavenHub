package com.OYO.HotelManagment.DTO.Response;

import com.OYO.HotelManagment.Enum.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {

   private String errorMessage;

   private String name;

   private String contactNumber;

   private String email;

   private String status;


}
