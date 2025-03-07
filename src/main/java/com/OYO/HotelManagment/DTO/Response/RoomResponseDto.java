package com.OYO.HotelManagment.DTO.Response;

import com.OYO.HotelManagment.Enum.Roomtype;
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
public class RoomResponseDto {

   private String errorMessage;

   private String amenities;

   private Integer maxOccupancy;

   private Roomtype roomtype;

   private Boolean isActive;

   private String created;
}
