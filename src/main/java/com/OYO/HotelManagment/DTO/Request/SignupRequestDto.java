package com.OYO.HotelManagment.DTO.Request;

import lombok.Data;

@Data
public class SignupRequestDto{

    private String name;
    private String email;
    private String contactNumber;
    private String password;
}
