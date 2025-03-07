package com.OYO.HotelManagment.DTO.Request;

import com.OYO.HotelManagment.Enum.Roomtype;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class RoomRequestDTO {

   private String amenities;
   private Integer maxOccupancy;
   private Roomtype roomtype;
//   private boolean active;

}
