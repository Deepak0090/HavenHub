package com.OYO.HotelManagment.DTO;

import lombok.Data;

@Data
public class VerifyOtpDto {
    private String email;
    private String otp;
}
