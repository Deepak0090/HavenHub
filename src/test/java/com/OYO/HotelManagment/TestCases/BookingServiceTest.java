package com.OYO.HotelManagment.TestCases;

import com.OYO.HotelManagment.DTO.Request.BookingRequestDto;
import com.OYO.HotelManagment.DTO.Response.BookingResponseDto;
import com.OYO.HotelManagment.DTO.Response.HotelResponseDto;
import com.OYO.HotelManagment.DTO.Response.RoomResponseDto;
import com.OYO.HotelManagment.Enum.BookingStatus;
import com.OYO.HotelManagment.Enum.Roomtype;
import com.OYO.HotelManagment.Exception.*;
import com.OYO.HotelManagment.Model.Booking;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Service.*;
import com.OYO.HotelManagment.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepo bookingRepo;

    @Mock
    private CustomerService customerService;

    @Mock
    private PriceInventoryService priceInventoryService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HotelService hotelService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDto bookingRequestDto;
    private Booking booking;
    private Customer customer;

    @BeforeEach
    void setUp(){
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setHotelId(1);
        bookingRequestDto.setRoomId(101);
        bookingRequestDto.setCustomerID(1);
        bookingRequestDto.setCheckIn(LocalDate.now().plusDays(1));
        bookingRequestDto.setCheckOut(LocalDate.now().plusDays(5));
        bookingRequestDto.setIsPrepaid(true);

         customer = new Customer();
         customer.setId(1);

         booking = Booking.builder()
                 .hotelId(1)
                 .roomId(101)
                 .checkIn(bookingRequestDto.getCheckIn())
                 .checkOut(bookingRequestDto.getCheckOut())
                 .customer(customer)
                 .bookingStatus(BookingStatus.CREATED)
                 .amount(500.0)
                 .isPrepaid(true)
                 .build();
    }

    @Test
    void testCreateBooking_CheckInAfterCheckOut_ShouldThrowException() {
        bookingRequestDto.setCheckIn(LocalDate.now().plusDays(5));
        bookingRequestDto.setCheckOut(LocalDate.now().plusDays(1));

        assertThrows(CheckInAndCheckOutDateException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCreateBooking_CheckInInPast_ShouldThrowException() {
        bookingRequestDto.setCheckIn(LocalDate.now().minusDays(1));
        bookingRequestDto.setCheckOut(LocalDate.now().plusDays(2));

        assertThrows(CheckInAndCheckOutDateException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCreateBooking_NoRoomAvailable_ShouldThrowException() {
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCreateBooking_CustomerNotFound_ShouldThrowException() throws CustomerNotFoundException {
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(true);
        when(customerService.getCustomerById(anyInt())).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCreateBooking_HotelNotFound_ShouldThrowException() throws HotelNotFoundException, CustomerNotFoundException {
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(true);
        when(customerService.getCustomerById(anyInt())).thenReturn(customer);
        when(hotelService.getHotelDetails(anyInt())).thenReturn(null);

        assertThrows(HotelNotFoundException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCreateBooking_RoomNotFound_ShouldThrowException() throws CustomerNotFoundException, HotelNotFoundException, RoomNotFoundException {
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(true);
        when(customerService.getCustomerById(anyInt())).thenReturn(customer);
        when(hotelService.getHotelDetails(anyInt())).thenReturn(new HotelResponseDto());
        when(roomService.getRoomDetails(anyInt())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> {
            bookingService.createBookings(bookingRequestDto);
        });
    }

    @Test
    void testCancelBooking_BookingNotFound_ShouldThrowException() {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.cancelBooking(1);
        });
    }

    @Test
    void testCancelBooking_AlreadyCancelled_ShouldReturnFalse() throws BookingNotFoundException {
        booking.setBookingStatus(BookingStatus.CANCELLED);
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));

        boolean result = bookingService.cancelBooking(1);

        assertFalse(result);
    }

    @Test
    void testUpdateBooking_BookingNotFound_ShouldThrowException() {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.updateBookings(1, bookingRequestDto);
        });
    }

    @Test
    void testUpdateBooking_CheckInAfterCheckOut_ShouldThrowException() {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));
        bookingRequestDto.setCheckIn(LocalDate.now().plusDays(10));
        bookingRequestDto.setCheckOut(LocalDate.now().plusDays(5));

        assertThrows(CheckInAndCheckOutDateException.class, () -> {
            bookingService.updateBookings(1, bookingRequestDto);
        });
    }

    @Test
    void testUpdateBooking_RoomUnavailable_ShouldThrowException() {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(false);

        assertThrows(RoomNotFoundException.class, () -> {
            bookingService.updateBookings(1, bookingRequestDto);
        });
    }
    @Test
    void testGetBookingDetails_Success() throws BookingNotFoundException {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingResponseDto response = bookingService.getBookingDetails(1);

        assertNotNull(response);
        assertEquals(booking.getHotelId(), response.getHotelId());
    }

    @Test
    void testGetBookingDetails_NotFound_ShouldThrowException() {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getBookingDetails(1);
        });
    }

    @Test
    void testGetAllBookingDetails_Success() {
        when(bookingRepo.findAll()).thenReturn(List.of(booking));

        List<BookingResponseDto> response = bookingService.getAllBookingDetails();

        assertEquals(1, response.size());
        assertEquals(booking.getRoomId(), response.get(0).getRoomId());
    }


    @Test
    void testCreateBooking() throws Exception{

            when(hotelService.getHotelDetails(anyInt())).thenReturn(new HotelResponseDto());
            RoomResponseDto roomResponseDto = new RoomResponseDto();
            roomResponseDto.setRoomtype(Roomtype.DELUXE);
            when(roomService.getRoomDetails(anyInt())).thenReturn(roomResponseDto);
            when(priceInventoryService.checkAvailable(anyInt(), anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
            when(customerService.getCustomerById(anyInt())).thenReturn(customer);
            when(priceInventoryService.calculatePrice(anyInt(),anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(500.0);
            when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto responseDto = bookingService.createBookings(bookingRequestDto);

        assertNotNull(responseDto);
        assertEquals(booking.getHotelId(), responseDto.getHotelId());
    }

    @Test
    void testUpdateBooking_Successful() throws Exception {
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));
        when(priceInventoryService.checkAvailable(anyInt(), anyInt(), any(), any())).thenReturn(true);
        when(priceInventoryService.calculatePrice(anyInt(), anyInt(), any(), any())).thenReturn(600.0);
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto response = bookingService.updateBookings(1, bookingRequestDto);

        assertNotNull(response);
        assertEquals(bookingRequestDto.getRoomId(), response.getRoomId());
    }

    @Test
    void  TestCancelBooking() throws BookingNotFoundException{
        when(bookingRepo.findById(anyInt())).thenReturn(Optional.of(booking));


        boolean result = bookingService.cancelBooking(1);
        assertTrue(result);
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }


}
