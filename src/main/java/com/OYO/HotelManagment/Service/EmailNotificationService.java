package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.interfaces.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Primary
@Component
public class EmailNotificationService implements NotificationService {

    @Autowired
    public JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendNotification(NotificationDto notificationDto){

        System.out.println("Sending email notification in thread: " + Thread.currentThread().getName());
        try {
             Thread.sleep(10000);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setTo(notificationDto.getCustomerEmail());
            helper.setText(notificationDto.getMessage(),true);
            helper.setSubject(notificationDto.getSubject());

            javaMailSender.send(mimeMessage);
            System.out.println("✅ Email Sent Successfully!");
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send HTML email: " + e.getMessage());
            throw new RuntimeException(e);
        }
//         SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//         simpleMailMessage.setTo(notificationDto.getCustomerEmail());
//         simpleMailMessage.setText(notificationDto.getMessage());
//         simpleMailMessage.setSubject(notificationDto.getSubject());

//       javaMailSender.send(simpleMailMessage);


//        System.out.println("✅ Email Sent Successfully!");
    }
}
