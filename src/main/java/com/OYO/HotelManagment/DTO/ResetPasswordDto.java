package com.OYO.HotelManagment.DTO;

import lombok.Data;

@Data
public class ResetPasswordDto {
    private String email;
    private String newPassword;
}
