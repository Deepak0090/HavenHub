package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppNotificationService implements NotificationService {

    @Override
    public void sendNotification(NotificationDto notificationDto){

        System.out.println("Send WhatsApp Notification");

    }
}
