package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Request.RoomRequestDTO;
import com.OYO.HotelManagment.DTO.Response.RoomResponseDto;
import com.OYO.HotelManagment.Exception.RoomNotFoundException;
import com.OYO.HotelManagment.Model.Room;
import com.OYO.HotelManagment.Repository.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    RoomRepo roomRepo;

   public RoomResponseDto createRooms(RoomRequestDTO roomRequestDTO){
       Room room = convertRoomRequestDTORoom(roomRequestDTO);
       RoomResponseDto responseDto = new RoomResponseDto();
       responseDto.setIsActive(room.getIsActive());
       responseDto.setRoomtype(room.getRoomtype());
       responseDto.setMaxOccupancy(room.getMaxOccupancy());
       responseDto.setAmenities(room.getAmenities());
       responseDto.setCreated("The Room is Successfully Added in your Hotel");
       roomRepo.save(room);
       return responseDto;
   }

    private Room convertRoomRequestDTORoom(RoomRequestDTO roomRequestDTO) {

        Room room = new Room();
        room.setAmenities(roomRequestDTO.getAmenities());
        room.setMaxOccupancy(roomRequestDTO.getMaxOccupancy());
        room.setRoomtype(roomRequestDTO.getRoomtype());
        return room;
    }

    public RoomResponseDto getRoomDetails(Integer roomId) throws RoomNotFoundException {
        Optional<Room> room = roomRepo.findById(roomId);
        if (!room.isPresent()){
            throw new RoomNotFoundException("Room with this roomId: "+roomId+" Does Not Present");
        }
        return convertRoomResponseDtoRoom(room.get());
    }
    public List<RoomResponseDto> getAllRoom(){
       List<Room> rooms = roomRepo.findAll();
       List<RoomResponseDto> responseDtoList = new ArrayList<>();
       for (Room room : rooms){
           responseDtoList.add(convertRoomResponseDtoRoom(room));
       }
       return responseDtoList;
    }

    private RoomResponseDto convertRoomResponseDtoRoom(Room room) {
       return RoomResponseDto.builder().amenities(room.getAmenities())
               .maxOccupancy(room.getMaxOccupancy())
               .roomtype(room.getRoomtype())
               .isActive(room.getIsActive())
               .build();

    }
}
