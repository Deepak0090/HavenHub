package com.OYO.HotelManagment.interfaces;


import com.OYO.HotelManagment.DTO.NotificationDto;
import lombok.Builder;
import org.springframework.stereotype.Component;



public interface NotificationService{

    void sendNotification(NotificationDto notificationDto);
}
