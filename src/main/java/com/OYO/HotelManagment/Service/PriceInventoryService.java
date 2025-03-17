package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Response.PriceInventoryResponseDto;
import com.OYO.HotelManagment.Model.Hotel;
import com.OYO.HotelManagment.Model.PriceInventoryDetails;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Repository.PriceInventoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PriceInventoryService {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    private PriceInventoryRepo priceInventoryRepo;

    // public void updateInventory(){ -> need to create this Function

    public boolean updateInventory(Integer hotelId, Integer roomId, LocalDate checkIn, LocalDate checkOut, boolean isCancel){

        Optional<PriceInventoryDetails> inventoryOpt  = priceInventoryRepo.findByHotelIdAndRoomIdAndDate(hotelId, roomId, checkIn);
        if (inventoryOpt.isPresent()){
            PriceInventoryDetails inventoryDetails = inventoryOpt.get();
            if (isCancel){
                inventoryDetails.setAvailableRooms(inventoryDetails.getAvailableRooms()+1);
            }else {
                if (inventoryDetails.getAvailableRooms()> 0){
                    inventoryDetails.setAvailableRooms(inventoryDetails.getAvailableRooms()- 1);
                }else {
                    return false;
                }

            }
            priceInventoryRepo.save(inventoryDetails);
            return true;

        }
        return false;
    }

    public List<PriceInventoryResponseDto> getAvailableHotelsByMinPrice(List<Hotel> hotelList, LocalDate checkIn){
        List<PriceInventoryResponseDto> responseDtoList = new ArrayList<>();
        for (Hotel hotel : hotelList){
            List<PriceInventoryDetails> inventoryDetailsList = priceInventoryRepo.findByHotelIdAndDate(hotel.getId(),checkIn);

            if (!inventoryDetailsList.isEmpty()){
                PriceInventoryDetails minPriceInventory = inventoryDetailsList.stream()
                        .min((a,b) -> Integer.compare(a.getPrice(), b.getPrice()))
                        .orElse(null);
                if (minPriceInventory != null){
                    responseDtoList.add(convertToResponseDto(minPriceInventory));
                }
            }
        }
        return responseDtoList;
    }
    public List<PriceInventoryResponseDto> getPriceInventoryForHotel(Integer hotelId, LocalDate checkIn, LocalDate checkOut){
        List<PriceInventoryResponseDto> priceInventoryResponseDtoList = new ArrayList<>();
        List<PriceInventoryDetails> priceInventoryDetails = priceInventoryRepo.findByHotelIdAndDateBetween(hotelId,checkIn,checkOut);
        for (PriceInventoryDetails priceInventoryDetails1 : priceInventoryDetails){
            PriceInventoryResponseDto responseDto = convertToResponseDto(priceInventoryDetails1);
            priceInventoryResponseDtoList.add(responseDto);
        }
        return priceInventoryResponseDtoList;
    }

    private PriceInventoryResponseDto convertToResponseDto(PriceInventoryDetails priceInventoryDetails1) {
        boolean isSoledOut = priceInventoryDetails1.getAvailableRooms()<=0;
        return PriceInventoryResponseDto.builder()
                .price(priceInventoryDetails1.getPrice())
                .date(priceInventoryDetails1.getDate())
                .hotelId(priceInventoryDetails1.getHotelId())
                .roomId(priceInventoryDetails1.getRoomId())
                .isSoldOut(isSoledOut)
                .build();
    }

    public boolean checkAvailable(Integer hotelId, Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        System.out.println("Checking availability for Hotel ID: " + hotelId + ", Room ID: " + roomId +
                ", Check-In: " + checkIn + ", Check-Out: " + checkOut);
        Optional<PriceInventoryDetails> inventoryDetails = priceInventoryRepo.findByHotelIdAndRoomIdAndDate(hotelId,roomId,checkIn);
        if (!inventoryDetails.isPresent()) {
            System.out.println("No inventory found for the given details.");
            return false;
        }
            PriceInventoryDetails inventoryDetails1 = inventoryDetails.get();
            if (inventoryDetails1.getAvailableRooms()<=0){
                System.out.println("Rooms are sold out for the given date.");
                return false;
            }
            long count = bookingRepo.countByHotelIdAndRoomIdAndCheckInBetween(hotelId,roomId,checkIn,checkOut);
            if (count>0){
                System.out.println("Room is already booked for the given dates");
                return false;
            }
            return true;

    }

    public double calculatePrice(Integer hotelId, Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        Optional<PriceInventoryDetails> inventoryDetails = priceInventoryRepo.findByHotelIdAndRoomIdAndDate(hotelId,roomId,checkIn);
        if (inventoryDetails.isPresent()){
            PriceInventoryDetails inventoryDetails1 = inventoryDetails.get();
            long daysBetween = ChronoUnit.DAYS.between(checkIn, checkOut);
            return inventoryDetails.get().getPrice()*daysBetween;
        }
        throw new IllegalArgumentException("Room Price Information is Not Found");
    }

    // need to write the Query which will Decrees the Inventory
//    priceInventoryRepo.
    // }

//    public List<PriceInventoryResponseDto> getAvailableHotelsByMinPrice(List<Hotel> hotelList, LocalDate checkIn){
//        return  null;
//    }
//
//        // need to edit this logic
//    public List<PriceInventoryResponseDto> getPriceAndInventoryForHotel(Integer hotelId, LocalDate checkIn, LocalDate checkOut){
//        List<PriceInventoryResponseDto> priceInventoryResponseDtoList = new ArrayList<>();
//        List<PriceInventoryDetails> priceInventoryDetails = priceInventoryRepo.findByHotelIdAndCheckIn(hotelId,checkIn,checkOut);
//        for (PriceInventoryDetails priceInventoryDetails1 :priceInventoryDetails){
//            PriceInventoryResponseDto responseDto = convertPriceInventoryDetailsToPriceInventoryResponseDto(priceInventoryDetails1);
//            priceInventoryResponseDtoList.add(responseDto);
//        }
//        return priceInventoryResponseDtoList;
//    }
//
//    private PriceInventoryResponseDto convertPriceInventoryDetailsToPriceInventoryResponseDto(PriceInventoryDetails priceInventoryDetails) {
//
//        Boolean isSoledOut=isHotelSoledOut(priceInventoryDetails.getAvailableRooms());
//      return   PriceInventoryResponseDto.builder().price(priceInventoryDetails.getPrice())
//                .date(priceInventoryDetails.getDate())
//                .roomId(priceInventoryDetails.getRoomId())
//                .hotelId(priceInventoryDetails.getHotelId())
//                .isSoldOut(isSoledOut)
//                .build();
//    }

//
//    private Boolean isHotelSoledOut(Integer availableRooms) {
//        return  availableRooms<0;
//    }

}
