package com.OYO.HotelManagment.Exception;

public class DuplicateEmailException extends Exception{
    public DuplicateEmailException(String errorMessage){
        super(errorMessage);
    }
}
