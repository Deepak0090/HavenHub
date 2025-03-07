package com.OYO.HotelManagment.DTO.Response;

import com.OYO.HotelManagment.Model.Room;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponseDto {

     private String errorMessage;
     private String hotelName;
     private String address;
     private Boolean Status;
     private String Created;
     private List<Room> rooms;

}
