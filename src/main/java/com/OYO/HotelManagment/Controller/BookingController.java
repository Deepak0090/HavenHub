package com.OYO.HotelManagment.Controller;


import com.OYO.HotelManagment.DTO.Request.BookingRequestDto;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.Exception.*;
import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    BookingService bookingService;

        @PostMapping("/create")
        public ResponseEntity<BookingResponseDto> createBooking(@RequestBody BookingRequestDto bookingRequestDto){
            try {
                BookingResponseDto responseDto =   bookingService.createBookings(bookingRequestDto);
                return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
            } catch (RoomNotFoundException | CustomerNotFoundException | CheckInAndCheckOutDateException |
                     HotelNotFoundException e) {
                BookingResponseDto bookingResponseDto = new BookingResponseDto();
                bookingResponseDto.setErrorMessage(e.getMessage());
                return  new ResponseEntity<>(bookingResponseDto, HttpStatus.BAD_REQUEST);
            }

        }

        @GetMapping("/")
        public ResponseEntity<BookingResponseDto> getBookingDetails(@RequestParam Integer bookingId){
            try {
                BookingResponseDto bookingResponseDto = bookingService.getBookingDetails(bookingId);
                return new ResponseEntity<>(bookingResponseDto,HttpStatus.OK);
            }catch (BookingNotFoundException e){
                BookingResponseDto bookingResponseDto = new BookingResponseDto();
                bookingResponseDto.setErrorMessage(e.getMessage());
                return new ResponseEntity<>(bookingResponseDto,HttpStatus.BAD_REQUEST);
            }
        }
        @GetMapping("/allBookings")
        public List<BookingResponseDto> getAllBookingDetails(){
            return bookingService.getAllBookingDetails();
        }

        @DeleteMapping("/cancel/{bookingId}")
        public ResponseEntity<String> cancelBooking(@PathVariable Integer bookingId){
            try {
                boolean isCancelled = bookingService.cancelBooking(bookingId);
                if (isCancelled){
                    return ResponseEntity.ok("Booking cancelled successfully.");
                }else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking is already cancelled");
                }
            } catch (BookingNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }

        @PutMapping("/update/{bookingId}")
        public ResponseEntity<?> updateBooking(@PathVariable Integer bookingId, @RequestBody BookingRequestDto bookingRequestDto) throws BookingNotFoundException, RoomNotFoundException, CheckInAndCheckOutDateException {
            try {
                BookingResponseDto updateBooking = bookingService.updateBookings(bookingId,bookingRequestDto);
                    return ResponseEntity.ok(updateBooking);
            }catch (BookingNotFoundException | CheckInAndCheckOutDateException |RoomNotFoundException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

            }
        }

}
