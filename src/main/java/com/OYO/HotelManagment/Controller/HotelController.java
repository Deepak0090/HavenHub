package com.OYO.HotelManagment.Controller;

import com.OYO.HotelManagment.DTO.Request.HotelRequestDto;
import com.OYO.HotelManagment.DTO.Response.HotelResponseDto;
import com.OYO.HotelManagment.Exception.HotelLocationNotFoundException;
import com.OYO.HotelManagment.Exception.HotelNotFoundException;
import com.OYO.HotelManagment.Service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @PostMapping("/create")
    public ResponseEntity<HotelResponseDto> CreateHotel(@RequestBody  HotelRequestDto hotelRequestDto){
        HotelResponseDto responseDto = hotelService.CreateHotel(hotelRequestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<HotelResponseDto> getHotelDetails(@RequestParam Integer hotelId){
        try {
            HotelResponseDto hotelResponseDto = hotelService.getHotelDetails(hotelId);
            return new ResponseEntity<>(hotelResponseDto, HttpStatus.OK);
        }catch (HotelNotFoundException e){
            HotelResponseDto hotelResponseDto = new HotelResponseDto();
            hotelResponseDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(hotelResponseDto, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/Allhotels")
    public List<HotelResponseDto> getAllHotelDetails(){

        return hotelService.getAllHotel();
    }

    @GetMapping("/location")
    public ResponseEntity<Object> getHotelsByLocations(@RequestParam String location){
        try {
            List<HotelResponseDto> hotelResponseDtoList = hotelService.getAllHotelsByLocation(location);
            return new ResponseEntity<>(hotelResponseDtoList,HttpStatus.OK);
        }catch (HotelLocationNotFoundException e){
            Map<String, String> map = new HashMap<>();
            map.put("massage", e.getMessage());
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }

    }

}
