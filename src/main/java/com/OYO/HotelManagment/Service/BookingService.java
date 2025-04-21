package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.DTO.Request.BookingRequestDto;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.DTO.Response.HotelResponseDto;
import com.OYO.HotelManagment.DTO.Response.RoomResponseDto;
import com.OYO.HotelManagment.Enum.BookingStatus;
import com.OYO.HotelManagment.Exception.*;
import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    HotelService hotelService;

    @Autowired
    RoomService roomService;

    public BookingResponseDto createBookings(BookingRequestDto bookingRequestDto) throws RoomNotFoundException, CustomerNotFoundException, CheckInAndCheckOutDateException, HotelNotFoundException, BookingAmountNotFoundException {

        if (bookingRequestDto.getCheckIn().isAfter(bookingRequestDto.getCheckOut())) {
            throw new CheckInAndCheckOutDateException("Check-in Dated must be before Check-out Date");
        }
        if (bookingRequestDto.getCheckIn().isBefore(LocalDate.now())) {
            throw new CheckInAndCheckOutDateException("Check-in Date must be in the future.");
        }
        boolean isAvailable = priceInventoryService.checkAvailable(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut());

        if (!isAvailable) {
            throw new RoomNotFoundException("Sorry,Currently Rooms Are Not Available For The Selected Dates");
        }
        Customer customer = customerService.getCustomerById(bookingRequestDto.getCustomerID());

        if (customer == null) {
            throw new CustomerNotFoundException("Customer Not Found");
        }

        HotelResponseDto hotel = hotelService.getHotelDetails(bookingRequestDto.getHotelId());
        RoomResponseDto room = roomService.getRoomDetails(bookingRequestDto.getRoomId());

        if (hotel == null) {
            throw new HotelNotFoundException("Hotel Not Found");
        }
        if (room == null) {
            throw new RoomNotFoundException("Room Not Found");
        }



        double price = priceInventoryService.calculatePrice(
                bookingRequestDto.getHotelId(),
                bookingRequestDto.getRoomId(),
                bookingRequestDto.getCheckIn(),
                bookingRequestDto.getCheckOut()
        );
        if(bookingRequestDto.getBookingAmount()<price){
            throw new BookingAmountNotFoundException("Sorry! The provided amount does not match the expected room price.");
        }
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

        NotificationDto notificationDto = getNotificationSenderDto(saveBooking, customer, hotel, room);
        notificationService.sendNotification(notificationDto);


        return convertBookingResponseDto(saveBooking);
    }

    private BookingResponseDto convertBookingResponseDto(Booking booking){
        return BookingResponseDto.builder()
                .bookingId(booking.getId())
                .hotelId(booking.getHotelId())
                .roomId(booking.getRoomId())
                .customerId(booking.getCustomer().getId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .bookingAmount(booking.getAmount())
                .build();
    }


    private NotificationDto getNotificationSenderDto(Booking booking, Customer customer, HotelResponseDto hotel, RoomResponseDto room) {
        return NotificationDto.builder()
                .customerEmail(customer.getEmail())
                .bookingId(booking.getId())
                .hotelName(hotel.getHotelName())
                .hotelAddress(hotel.getAddress())
                .roomType(room.getRoomtype().toString())
                .checkInDate(booking.getCheckIn())
                .checkOutDate(booking.getCheckOut())
                .totalAmount(booking.getAmount())
                .Subject("BOOKING CONFIRMATION AT - " + hotel.getHotelName())
                .Message(
                        "Dear " + customer.getName() + ",\n\n"
                                + "Thank you for choosing " + hotel.getHotelName() + " !..\n\n"
                                + "Here are your booking details. Wishing you a pleasant stay!\n\n"
                                + " Booking ID : " + booking.getId() + "\n"
                                + " Hotel Name : " + hotel.getHotelName() + "\n"
                                + " Hotel Address : " + hotel.getAddress() + "\n"
                                + " Room Type : " + room.getRoomtype() + "\n"
                                + " Check-In Date : " + booking.getCheckIn() + "\n"
                                + " Check-Out Date : " + booking.getCheckOut() + "\n"
                                + " Total Amount: $ " + booking.getAmount() + "\n\n"
                                + "We look forward to welcoming you!\n\n"
                                + "Best Regards,\n\nHavenHub Management"
                )
                .build();
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
