package com.OYO.HotelManagment.Controller;

import com.OYO.HotelManagment.DTO.NotificationDto;
import com.OYO.HotelManagment.DTO.Request.ForgotPasswordReqDto;
import com.OYO.HotelManagment.DTO.Request.LoginReqDto;
import com.OYO.HotelManagment.DTO.Request.SignupRequestDto;
import com.OYO.HotelManagment.DTO.ResetPasswordDto;
import com.OYO.HotelManagment.DTO.VerifyOtpDto;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.CustomerRepo;
import com.OYO.HotelManagment.Service.EmailNotificationService;
import com.OYO.HotelManagment.interfaces.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@CrossOrigin(origins = "http://127.0.0.1:5500/")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    private Map<String,String> otpStore = new HashMap<>();

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequestDto){
        Optional<Customer> existingCustomer = customerRepo.findByEmail(signupRequestDto.getEmail());
        if (existingCustomer.isPresent()){
            return new ResponseEntity<>("Email is Already Exist", HttpStatus.BAD_REQUEST);
        }

         Customer customer = new Customer();
         customer.setName(signupRequestDto.getName());
         customer.setEmail(signupRequestDto.getEmail());
         customer.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
         customer.setContactNumber(signupRequestDto.getContactNumber());
         customer.setStatus("ACTIVE");
         customer.setCreatedAt(LocalDateTime.now());

         customerRepo.save(customer);
         return new ResponseEntity<>("Signup Successful",HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginReqDto loginReqDto){
        Optional<Customer> customer = customerRepo.findByEmail(loginReqDto.getEmail());

        if (customer.isEmpty()){
            return new ResponseEntity<>("your email is not registered!",HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(loginReqDto.getPassword(),customer.get().getPassword())){
            return new ResponseEntity<>("Invalid password", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("Login Successful",HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordReqDto forgotPasswordReqDto){
            System.out.println("=====> FORGOT PASSWORD API CALLED");
            String email = forgotPasswordReqDto.getEmail();

            Optional<Customer> customer = customerRepo.findByEmail(email);
            if (customer.isEmpty()){
                return new ResponseEntity<>("Oops Email is not registered!", HttpStatus.NOT_FOUND);
            }
        String otp = String.valueOf((int)((Math.random() * 900000) + 100000));
            otpStore.put(forgotPasswordReqDto.getEmail(),otp);

        NotificationDto dto = NotificationDto.builder()
                .customerEmail(forgotPasswordReqDto.getEmail())
                .Subject("HavenHub OTP Verification")
                .Message("<p>Hi there,</p>" +
                        "<p>We noticed a password reset request. Enter this code:</p>" +
                        "<p><strong style='font-size: 28px; color: #000;'>" + otp + "</strong></p>" +
                        "<p>Best,<br>HavenHub</p>")
                .build();


        notificationService.sendNotification(dto);
        return new ResponseEntity<>("OTP successfully sent to your email",HttpStatus.OK);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto){
        String correctOtp = otpStore.get(verifyOtpDto.getEmail());
        if (correctOtp==null || !correctOtp.equals(verifyOtpDto.getOtp())){
            return new ResponseEntity<>("Invalid OTP", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("OTP verified successfully",HttpStatus.OK);
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto req) {
        Optional<Customer> customerOpt = customerRepo.findByEmail(req.getEmail());
        if (customerOpt.isEmpty()) {
            return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
        }

        Customer customer = customerOpt.get();
        String encryptedPassword = new BCryptPasswordEncoder().encode(req.getNewPassword());
        customer.setPassword(encryptedPassword);
        customerRepo.save(customer);

        otpStore.remove(req.getEmail()); // Clear OTP after reset
        return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
    }
}
