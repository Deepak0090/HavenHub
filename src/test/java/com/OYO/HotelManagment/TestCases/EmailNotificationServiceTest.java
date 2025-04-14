package com.OYO.HotelManagment.TestCases;


import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.Service.EmailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @InjectMocks
    EmailNotificationService emailNotificationService;

    @Mock
    private JavaMailSender javaMailSender;


    @Captor
    private ArgumentCaptor <SimpleMailMessage> messageCaptor;


    @Test
    public void testSendNotification(){
        NotificationDto dto = NotificationDto.
                builder().customerEmail("dc022694@gmail.com").
                Subject("Just check the Test Cases")
                .Message("Hello Dear! This is Just for Test the Email")
                .build();

        emailNotificationService.sendNotification(dto);


       verify(javaMailSender,times(1)).send(messageCaptor.capture());

       SimpleMailMessage sentMassage = messageCaptor.getValue();
        assertEquals("dc022694@gmail.com",sentMassage.getTo()[0]);
        assertEquals("Just check the Test Cases",sentMassage.getSubject());
        assertEquals("Hello Dear! This is Just for Test the Email",sentMassage.getText());
    }
}
