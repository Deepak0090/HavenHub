package com.OYO.HotelManagment.Exception;

import org.aspectj.bridge.Message;

public class CheckInAndCheckOutDateException extends  Exception{

    public CheckInAndCheckOutDateException(String Message){
        super( Message);
    }
}
