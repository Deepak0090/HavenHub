package com.OYO.HotelManagment.Controller;

import com.OYO.HotelManagment.DTO.Request.RoomRequestDTO;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.DTO.Response.RoomResponseDto;
import com.OYO.HotelManagment.Exception.RoomNotFoundException;
import com.OYO.HotelManagment.Service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDTO roomRequestDTO){
        RoomResponseDto responseDto = roomService.createRooms(roomRequestDTO);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @GetMapping("/")
    public ResponseEntity<RoomResponseDto> getRoomDetails(@RequestParam Integer roomId){
        try {
            RoomResponseDto responseDto= roomService.getRoomDetails(roomId);
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }catch (RoomNotFoundException e){
            RoomResponseDto responseDto = new RoomResponseDto();
            responseDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/allrooms")
    public List<RoomResponseDto> getAllrooms(){

        return roomService.getAllRoom();
    }

}
