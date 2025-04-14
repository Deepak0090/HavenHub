package com.OYO.HotelManagment.TestCases;


import com.OYO.HotelManagment.DTO.Request.RoomRequestDTO;
import com.OYO.HotelManagment.DTO.Response.RoomResponseDto;
import com.OYO.HotelManagment.Enum.Roomtype;
import com.OYO.HotelManagment.Exception.RoomNotFoundException;
import com.OYO.HotelManagment.Model.Room;
import com.OYO.HotelManagment.Repository.RoomRepo;
import com.OYO.HotelManagment.Service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @InjectMocks
    RoomService roomService;

    @Mock
    RoomRepo roomRepo;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRooms(){

        RoomRequestDTO requestDTO = new RoomRequestDTO();
        requestDTO.setRoomtype(Roomtype.DELUXE);
        requestDTO.setMaxOccupancy(3);
        requestDTO.setAmenities("Wifi,TV");

        RoomResponseDto responseDto = roomService.createRooms(requestDTO);
        assertEquals(Roomtype.DELUXE,responseDto.getRoomtype());
        assertEquals(3, responseDto.getMaxOccupancy());
        assertEquals("Wifi,TV", responseDto.getAmenities());
        assertEquals("The Room is Successfully Added in your Hotel", responseDto.getCreated());

    }
    @Test
    public void testGetRoomDetails_Success() throws RoomNotFoundException{
        Room room = new Room();
        room.setRoomtype(Roomtype.CLASSIC);
        room.setAmenities("AC,Fridge");
        room.setMaxOccupancy(2);
        room.setIsActive(true);

        when(roomRepo.findById(1)).thenReturn(Optional.of(room));

        RoomResponseDto responseDto = roomService.getRoomDetails(1);
        assertEquals(Roomtype.CLASSIC,responseDto.getRoomtype());
        assertEquals("AC,Fridge", responseDto.getAmenities());
        assertEquals(2, responseDto.getMaxOccupancy());
        assertTrue(responseDto.getIsActive());
    }

    @Test
    public void testGetRoomDetails_NotFound(){
        when(roomRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class,()-> roomService.getRoomDetails(1));
    }

    @Test
    public void testGetAllRoom(){
        Room room1 = new Room();
        room1.setRoomtype(Roomtype.KING);
        room1.setMaxOccupancy(4);
        room1.setAmenities("WiFi,MiniBar");
        room1.setIsActive(true);

        Room room2 = new Room();
        room2.setRoomtype(Roomtype.QUEEN);
        room2.setMaxOccupancy(2);
        room2.setAmenities("WiFi,MiniBar");
        room2.setIsActive(false);

        when(roomRepo.findAll()).thenReturn(Arrays.asList(room1,room2));

        List<RoomResponseDto> roomList = roomService.getAllRoom();

        assertEquals(2,roomList.size());
        assertEquals(Roomtype.KING, roomList.get(0).getRoomtype());
        assertEquals(Roomtype.QUEEN,roomList.get(1).getRoomtype());
    }
}
