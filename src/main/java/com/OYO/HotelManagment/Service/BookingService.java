package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Request.BookingRequestDto;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.Enum.BookingStatus;
import com.OYO.HotelManagment.Exception.BookingNotFoundException;
import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    CustomerService customerService;

    @Autowired
    PriceInventoryService priceInventoryService;

    public BookingResponseDto createBookings(BookingRequestDto  bookingRequestDto) {
        // check validate request also check the req Parameter with throw the exception checkIn and Checkout date is valid or not
        // inventory available or nor that particular date , hotel , room? // and get Inventory priceInventoryService.checkAvailable()
        // create with status -> Created // bookingRepo.save(booking);
        // update the Inventory . need to Autowired PriceService in this for update the Inventory
        // priceInventoryService.updateInventory() -> also need crate this Function
        // return booking response;
        if (bookingRequestDto.getCheckIn().isAfter(bookingRequestDto.getCheckOut())){
            throw new IllegalArgumentException("Check-in Dated must be before Check-out Date");
        }
        if (bookingRequestDto.getCheckIn().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Check-in Date must be in the future.");
        }
        boolean isAvailable = priceInventoryService.checkAvailable(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut());

        if (!isAvailable){
            throw new IllegalArgumentException("Sorry,Currently Rooms Are Not Available For The Selected Dates");
        }
        Customer customer = customerService.getCustomerById(bookingRequestDto.getCustomerID());

        if (customer==null){
            throw new IllegalArgumentException("Customer Not Found");
        }
        double price = priceInventoryService.calculatePrice(
                 bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut()
        );
        Booking booking = Booking.builder().
                 hotelId(bookingRequestDto.getHotelId())
                .roomId(bookingRequestDto.getRoomId())
                .checkIn(bookingRequestDto.getCheckIn())
                .checkOut(bookingRequestDto.getCheckOut())
                .customer(customer)
                .bookingStatus(BookingStatus.CREATED)
                .amount(bookingRequestDto.getBookingAmount())
                .isPrepaid(bookingRequestDto.getIsPrepaid())
                .build();

        Booking saveBooking = bookingRepo.save(booking);

        priceInventoryService.updateInventory(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut()
        );


        return  convertBookingResponseDto(saveBooking);
    }

    private BookingResponseDto convertBookingResponseDto(Booking booking) {
      BookingResponseDto bookingResponseDto   =  BookingResponseDto.builder()
                .bookingAmount(booking.getAmount())
                .hotelId(booking.getHotelId())
                .bookingStatus(booking.getBookingStatus())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .isPrepaid(booking.getIsPrepaid())
                .roomId(booking.getRoomId())
                .build();

       return bookingResponseDto;
    }

    public Boolean cancelBooking(Integer  bookingId){

        // validate if booking is not in CANCELLED state and is present in our database
        // status -> CANCEL -> bookingrepo.save();
        // increase Inventory -> priceInventoryService.updateInventory();

        return true; // Dto is SepRated
    }

    public BookingResponseDto updateBookings(BookingRequestDto bookingRequestDto){
        return  null; // need to create the logic
    }


    }
