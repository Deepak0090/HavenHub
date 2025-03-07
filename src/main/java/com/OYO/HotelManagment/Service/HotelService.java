package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Request.HotelRequestDto;
import com.OYO.HotelManagment.DTO.Response.HotelResponseDto;
import com.OYO.HotelManagment.Exception.HotelLocationNotFoundException;
import com.OYO.HotelManagment.Exception.HotelNotFoundException;
import com.OYO.HotelManagment.Model.Hotel;
import com.OYO.HotelManagment.Repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    HotelRepository hotelRepository;

    public HotelResponseDto CreateHotel(HotelRequestDto hotelRequestDto){
        Hotel hotel = ConvertHotelRequestDtoToHotel(hotelRequestDto);
        HotelResponseDto hotelResponseDto = new HotelResponseDto();
        hotelResponseDto.setHotelName(hotel.getHotelName());
        hotelResponseDto.setAddress(hotel.getAddress());
        hotelResponseDto.setStatus(hotel.getStatus());
        hotelResponseDto.setCreated("Your Hotel Is Successfully Onboard in Our Website");
        hotelRepository.save(hotel);
        return hotelResponseDto;
    }

    public Hotel ConvertHotelRequestDtoToHotel(HotelRequestDto hotelRequestDto) {
            Hotel hotel = new Hotel();

           hotel.setHotelName(hotelRequestDto.getHotelName());
           hotel.setAddress(hotelRequestDto.getAddress());
           hotel.setContactNumber(hotelRequestDto.getContactNumber());
           hotel.setOnbordDate(LocalDateTime.now());
           hotel.setStatus(
                   hotelRequestDto.getStatus() !=null? hotelRequestDto.getStatus() : true);
           return hotel;

    }
    public List<HotelResponseDto> getAllHotel(){
        List<Hotel> hotels = hotelRepository.findAll();
        List<HotelResponseDto> responseDtoList = new ArrayList<>();
        for (Hotel hotel : hotels){
            responseDtoList.add(CovertHotelToResponseDto(hotel));
        }
        return responseDtoList;

    }

    public HotelResponseDto getHotelDetails(Integer hotelId) throws HotelNotFoundException {
       Optional<Hotel> hotels= hotelRepository.findById(hotelId);

       if (!hotels.isPresent()){
           throw new HotelNotFoundException("Hotel with this hotelId: "+hotelId+" does not exist");
       }
       return  CovertHotelToResponseDto(hotels.get());
    }

    private HotelResponseDto CovertHotelToResponseDto(Hotel hotel) {
        return HotelResponseDto.builder().hotelName(hotel.getHotelName())
                .rooms(hotel.getRoomList())
                .address(hotel.getHotelName())
                .Status(hotel.getStatus())
                .build();

    }


    public List<HotelResponseDto> getAllHotelsByLocation(String location) throws HotelLocationNotFoundException {
          List<Hotel> hotelList = hotelRepository.findByAddressContainingIgnoreCase(location);
          if (hotelList.isEmpty()){
              throw new HotelLocationNotFoundException("No Hotels found for the given location: "+location);
          }
          return hotelList.stream()
                  .map(this::convertToResponseDto)
                  .collect(Collectors.toList());
    }

    private HotelResponseDto convertToResponseDto(Hotel hotel) {
        HotelResponseDto responseDto = new HotelResponseDto();
        responseDto.setHotelName(hotel.getHotelName());
        responseDto.setAddress(hotel.getAddress());
        responseDto.setStatus(hotel.getStatus());
        return responseDto;
    }

}