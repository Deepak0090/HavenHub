package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.DTO.Request.BookingRequestDto;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.Enum.BookingStatus;
import com.OYO.HotelManagment.Exception.BookingNotFoundException;
import com.OYO.HotelManagment.Exception.CheckInAndCheckOutDateException;
import com.OYO.HotelManagment.Exception.CustomerNotFoundException;
import com.OYO.HotelManagment.Exception.RoomNotFoundException;
import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.interfaces.NotificationService;
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

    @Autowired
    NotificationService notificationService;

    public BookingResponseDto createBookings(BookingRequestDto  bookingRequestDto) throws RoomNotFoundException, CustomerNotFoundException, CheckInAndCheckOutDateException {

        if (bookingRequestDto.getCheckIn().isAfter(bookingRequestDto.getCheckOut())){
            throw new CheckInAndCheckOutDateException("Check-in Dated must be before Check-out Date");
        }
        if (bookingRequestDto.getCheckIn().isBefore(LocalDate.now())){
            throw new CheckInAndCheckOutDateException("Check-in Date must be in the future.");
        }
        boolean isAvailable = priceInventoryService.checkAvailable(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut());

        if (!isAvailable){
            throw new RoomNotFoundException("Sorry,Currently Rooms Are Not Available For The Selected Dates");
        }
        Customer customer = customerService.getCustomerById(bookingRequestDto.getCustomerID());

        if (customer==null){
            throw new CustomerNotFoundException("Customer Not Found");
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
                .amount(Double.valueOf(price))
                .isPrepaid(bookingRequestDto.getIsPrepaid())
                .build();

        Booking saveBooking = bookingRepo.save(booking);

        priceInventoryService.updateInventory(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut(),
                false
        );
        System.out.println(Thread.currentThread());
        NotificationDto notificationDto = getNotificationSenderDto();
        notificationService.sendNotification(notificationDto);
        System.out.println(Thread.currentThread());


        return  convertBookingResponseDto(saveBooking);
    }

    private NotificationDto getNotificationSenderDto() {
       NotificationDto notificationDto = new NotificationDto();
       notificationDto.setMessage("Hey Your Booking is successfully Created Please Find the Below Booking Details For Reference");
       notificationDto.setSubject("BOOKING CONFIRMATION");
       notificationDto.setCustomerEmail("deepak936408@gmail.com");

       return  notificationDto;

    }

    private BookingResponseDto convertBookingResponseDto(Booking booking) {
      BookingResponseDto bookingResponseDto   =  BookingResponseDto.builder()
              .bookingId(booking.getId())
              .hotelId(booking.getHotelId())
              .roomId(booking.getRoomId())
              .customerId(booking.getCustomer().getId())
              .checkIn(booking.getCheckIn())
              .checkOut(booking.getCheckOut())
              .bookingAmount(booking.getAmount())
              .message("Booking successfully created!")
                .build();

       return bookingResponseDto;
    }

    public Boolean cancelBooking(Integer  bookingId) throws BookingNotFoundException {

        // validate if booking is not in CANCELLED state and is present in our database
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (!booking.isPresent()){
            throw new BookingNotFoundException("No Booking Exist For this Booking ID: "+bookingId);
        }
        Booking booking1 = booking.get();
        if (booking1.getBookingStatus()==BookingStatus.CANCELLED){
            return false;
        }
        // status -> CANCEL -> bookingrepo.save();
        booking1.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking1);

        // increase Inventory -> priceInventoryService.updateInventory();
        priceInventoryService.updateInventory(
                booking1.getHotelId(),
                booking1.getRoomId(),
                booking1.getCheckIn(),
                booking1.getCheckOut(),
                true
        );
        return true;
    }

    public BookingResponseDto updateBookings( Integer bookingId, BookingRequestDto bookingRequestDto) throws BookingNotFoundException, CheckInAndCheckOutDateException, RoomNotFoundException {
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (!booking.isPresent()){
            throw new BookingNotFoundException("No Booking Found for ID: "+bookingId);
        }
         Booking booking1 = booking.get();
        if (bookingRequestDto.getCheckIn().isAfter(bookingRequestDto.getCheckOut())){
            throw new CheckInAndCheckOutDateException("Check in Date must be before Check-out Date");
        }
        boolean isAvailable = priceInventoryService.checkAvailable(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut()
        );
        if (!isAvailable){
            throw new RoomNotFoundException("Sorry, for selected room is not available for the new dates");
        }
        priceInventoryService.updateInventory(
                booking1.getHotelId(),
                booking1.getRoomId(),
                booking1.getCheckIn(),
                booking1.getCheckOut(),
                true
        );

           booking1.setHotelId(bookingRequestDto.getHotelId());
           booking1.setRoomId(bookingRequestDto.getRoomId());
           booking1.setCheckIn(bookingRequestDto.getCheckIn());
           booking1.setCheckOut(bookingRequestDto.getCheckOut());
           booking1.setAmount(priceInventoryService.calculatePrice(
                   bookingRequestDto.getHotelId(),
                   bookingRequestDto.getRoomId(),
                   bookingRequestDto.getCheckIn(),
                   bookingRequestDto.getCheckOut()
           ));
           Booking updateBooking = bookingRepo.save(booking1);

           priceInventoryService.updateInventory(
                   bookingRequestDto.getHotelId(),
                   bookingRequestDto.getRoomId(),
                   bookingRequestDto.getCheckIn(),
                   bookingRequestDto.getCheckOut(),
                   true
           );
        return  convertBookingResponseDto(updateBooking);

    }

    public List<BookingResponseDto> getAllBookingDetails() {
        List<Booking> booking = bookingRepo.findAll();
        List<BookingResponseDto> bookingResponseDtoList = new ArrayList<>();
        for (Booking booking1 : booking){
            bookingResponseDtoList.add(convertBookingResponseDto(booking1));
        }
        return bookingResponseDtoList;
    }

    public BookingResponseDto getBookingDetails(Integer bookingId) throws BookingNotFoundException {

       Optional<Booking> booking = bookingRepo.findById(bookingId);
       if (!booking.isPresent()){
           throw new BookingNotFoundException("Oops No Booking Exist In Our Data for this Booking Id: "+bookingId);
       }
       return convertBookingResponseDto(booking.get());
    }

}
