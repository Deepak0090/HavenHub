package com.OYO.HotelManagment.TestCases;

import com.OYO.HotelManagment.DTO.Request.HotelRequestDto;
import com.OYO.HotelManagment.DTO.Response.HotelResponseDto;
import com.OYO.HotelManagment.Exception.HotelLocationNotFoundException;
import com.OYO.HotelManagment.Exception.HotelNotFoundException;
import com.OYO.HotelManagment.Model.Hotel;
import com.OYO.HotelManagment.Repository.HotelRepository;
import com.OYO.HotelManagment.Service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @InjectMocks
    private HotelService hotelService;

    @Mock
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateHotel(){
        HotelRequestDto requestDto = new HotelRequestDto();
        requestDto.setHotelName("OYO Grand");
        requestDto.setAddress("Delhi");
        requestDto.setContactNumber("902748929");

        HotelResponseDto responseDto = hotelService.CreateHotel(requestDto);

        assertEquals("OYO Grand", responseDto.getHotelName());
        assertEquals("Delhi", responseDto.getAddress());
        assertTrue(responseDto.getStatus());
        assertEquals("Your Hotel Is Successfully Onboard in Our Website",responseDto.getCreated());
    }

    @Test
    public void testGetAllHotel(){
        Hotel hotel = new Hotel();
        hotel.setHotelName("OYO Blue");
        hotel.setAddress("Gurgaon");
        hotel.setStatus(true);
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelResponseDto> hotels = hotelService.getAllHotel();

        assertEquals(1, hotels.size());
        assertEquals("OYO Blue", hotels.get(0).getHotelName());
    }

    @Test
    public void testGetHotelDetails_Success() throws HotelNotFoundException{
        Hotel hotel = new Hotel();
        hotel.setHotelName("OYO Paradise");
        hotel.setAddress("Bangalore");
        hotel.setStatus(true);

        when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel));

        HotelResponseDto hotelResponseDto = hotelService.getHotelDetails(1);
        assertEquals("OYO Paradise", hotelResponseDto.getHotelName());
    }

    @Test
    public void testGetHotelDetails_NotFound(){
        when(hotelRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,()->{
            hotelService.getHotelDetails(99);
        });
    }

    @Test
    public void testGetAllHotelsByLocation_Success() throws HotelLocationNotFoundException {
        Hotel hotel = new Hotel();
        hotel.setHotelName("Taj");
        hotel.setAddress("Gurgaon");
        hotel.setStatus(true);

        when(hotelRepository.findByAddressContainingIgnoreCase("Gurgaon")).thenReturn(List.of(hotel));

        List<HotelResponseDto> hotels = hotelService.getAllHotelsByLocation("Gurgaon");
        assertEquals(1, hotels.size());
        assertEquals("Taj",hotels.get(0).getHotelName());

    }

    @Test
    public void testGetAllHotelsByLocation_NotFound(){
       when(hotelRepository.findByAddressContainingIgnoreCase("NoWhere")).thenReturn(List.of());

        assertThrows(HotelLocationNotFoundException.class,()->{
           hotelService.getAllHotelsByLocation("NoWhere");
       });

    }


}
