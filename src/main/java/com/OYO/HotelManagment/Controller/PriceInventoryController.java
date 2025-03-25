package com.OYO.HotelManagment.Controller;

import com.OYO.HotelManagment.DTO.Response.PriceInventoryResponseDto;
import com.OYO.HotelManagment.Service.PriceInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class PriceInventoryController{

    @Autowired
    PriceInventoryService priceInventoryService;

    List<PriceInventoryResponseDto> getPriceForHotel(@RequestParam("hotelId") Integer hotelId, @RequestParam("checkIn")LocalDate checkIn, @RequestParam("checkOut") LocalDate checkOut){
        return priceInventoryService.getPriceAndInventoryForHotel(hotelId, checkIn,checkOut);
    }
}
