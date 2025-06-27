package com.OYO.HotelManagment.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private String customerEmail;
    private Integer bookingId;
    private String hotelName;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalAmount;
    private String hotelAddress;
    private  String Subject;
    private String Message;


    public NotificationDto(String email, String message, String subject) {
        this.customerEmail = email;
        this.Message = message;
        this.Subject = subject;
    }

}
