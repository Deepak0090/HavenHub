package com.OYO.HotelManagment.TestCases;


import com.OYO.HotelManagment.DTO.Response.PriceInventoryResponseDto;
import com.OYO.HotelManagment.Model.Hotel;
import com.OYO.HotelManagment.Model.PriceInventoryDetails;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Repository.PriceInventoryRepo;
import com.OYO.HotelManagment.Service.PriceInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceInventoryServiceTest {

    @InjectMocks
    private PriceInventoryService priceInventoryService;

    @Mock
    private PriceInventoryRepo priceInventoryRepo;

    @Mock
    private BookingRepo bookingRepo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateInventoryBookingSuccess() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);

        PriceInventoryDetails day1 = new PriceInventoryDetails();
        day1.setAvailableRooms(2);

        PriceInventoryDetails day2 = new PriceInventoryDetails();
        day2.setAvailableRooms(2);

        PriceInventoryDetails day3 = new PriceInventoryDetails();
        day3.setAvailableRooms(2);


        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    LocalDate date = invocation.getArgument(2);
                    if (date.equals(checkIn)) return Optional.of(day1);
                    else if (date.equals(checkIn.plusDays(1))) return Optional.of(day2);
                    else if (date.equals(checkIn.plusDays(2))) return Optional.of(day3);
                    else return Optional.empty();
                });

        boolean result = priceInventoryService.updateInventory(1, 1, checkIn, checkOut, false);

        assertTrue(result); // Should be true now since all 3 days have inventory
        verify(priceInventoryRepo, times(3)).save(any());
    }

    @Test
    public void testUpdateInventoryCancelBooking() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        PriceInventoryDetails details = new PriceInventoryDetails();
        details.setAvailableRooms(1);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.of(details));

        boolean result = priceInventoryService.updateInventory(1, 1, checkIn, checkOut, true);
        assertTrue(result);
    }

    @Test
    public void testUpdateInventoryFailureDueToNoRooms() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        PriceInventoryDetails details = new PriceInventoryDetails();
        details.setAvailableRooms(0);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.of(details));

        boolean result = priceInventoryService.updateInventory(1, 1, checkIn, checkOut, false);
        assertFalse(result);
    }

    @Test
    public void testGetAvailableHotelsByMinPrice() {
        LocalDate checkIn = LocalDate.now();
        Hotel hotel = new Hotel();
        hotel.setId(1);

        PriceInventoryDetails detail = new PriceInventoryDetails();
        detail.setAvailableRooms(5);
        detail.setPrice(100);
        detail.setDate(checkIn);
        detail.setHotelId(1);
        detail.setRoomId(1);

        when(priceInventoryRepo.findByHotelIdAndDate(1, checkIn)).thenReturn(Collections.singletonList(detail));

        List<PriceInventoryResponseDto> result = priceInventoryService.getAvailableHotelsByMinPrice(Collections.singletonList(hotel), checkIn);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getPrice());
    }

    @Test
    public void testCheckAvailableTrue() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        PriceInventoryDetails details = new PriceInventoryDetails();
        details.setAvailableRooms(1);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.of(details));
        when(bookingRepo.countByHotelIdAndRoomIdAndCheckInBetween(anyInt(), anyInt(), any(), any())).thenReturn(0L);

        boolean available = priceInventoryService.checkAvailable(1, 1, checkIn, checkOut);
        assertTrue(available);
    }

    @Test
    public void testCheckAvailableFalseDueToZeroRooms() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        PriceInventoryDetails details = new PriceInventoryDetails();
        details.setAvailableRooms(0);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.of(details));

        boolean available = priceInventoryService.checkAvailable(1, 1, checkIn, checkOut);
        assertFalse(available);
    }

    @Test
    public void testCheckAvailableFalseDueToBookingExists() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        PriceInventoryDetails details = new PriceInventoryDetails();
        details.setAvailableRooms(2);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(Optional.of(details));
        when(bookingRepo.countByHotelIdAndRoomIdAndCheckInBetween(anyInt(), anyInt(), any(), any())).thenReturn(1L);

        boolean available = priceInventoryService.checkAvailable(1, 1, checkIn, checkOut);
        assertFalse(available);
    }

    @Test
    public void testCalculatePriceSuccess() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3);

        PriceInventoryDetails detail = new PriceInventoryDetails();
        detail.setPrice(200);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(1, 1, checkIn)).thenReturn(Optional.of(detail));

        double price = priceInventoryService.calculatePrice(1, 1, checkIn, checkOut);
        assertEquals(600, price);
    }

    @Test
    public void testCalculatePriceThrowsException() {
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);

        when(priceInventoryRepo.findByHotelIdAndRoomIdAndDate(1, 1, checkIn)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                priceInventoryService.calculatePrice(1, 1, checkIn, checkOut));
    }

    @Test
    public void testGetPriceAndInventoryForHotel() {
        Integer hotelId = 1;
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2);

        PriceInventoryDetails detail1 = new PriceInventoryDetails();
        detail1.setHotelId(hotelId);
        detail1.setRoomId(101);
        detail1.setDate(checkIn);
        detail1.setPrice(150);
        detail1.setAvailableRooms(5);

        PriceInventoryDetails detail2 = new PriceInventoryDetails();
        detail2.setHotelId(hotelId);
        detail2.setRoomId(101);
        detail2.setDate(checkIn.plusDays(1));
        detail2.setPrice(160);
        detail2.setAvailableRooms(0);

        List<PriceInventoryDetails> mockInventoryList = List.of(detail1, detail2);

        when(priceInventoryRepo.findByHotelIdAndDateBetween(hotelId, checkIn, checkOut)).thenReturn(mockInventoryList);

        List<PriceInventoryResponseDto> result = priceInventoryService.getPriceAndInventoryForHotel(hotelId, checkIn, checkOut);

        assertEquals(2, result.size());

        PriceInventoryResponseDto first = result.get(0);
        assertEquals(150, first.getPrice());
        assertFalse(first.getIsSoldOut());

        PriceInventoryResponseDto second = result.get(1);
        assertEquals(160, second.getPrice());
        assertTrue(second.getIsSoldOut());
    }
}
