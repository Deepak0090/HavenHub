package com.OYO.HotelManagment.Controller;

import com.OYO.HotelManagment.DTO.Request.CustomerRequestDto;
import com.OYO.HotelManagment.DTO.Response.CustomerResponseDto;
import com.OYO.HotelManagment.Exception.DuplicateEmailException;
import com.OYO.HotelManagment.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("/create")
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequestDto){
        try {


            CustomerResponseDto responseDto = customerService.createCustomers(customerRequestDto);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }catch (DuplicateEmailException e){
            CustomerResponseDto customerResponseDto = new CustomerResponseDto();
            customerResponseDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(customerResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/")
    public ResponseEntity<CustomerResponseDto> getCustomerDetails(@RequestParam  Integer customerId){
        try {
            CustomerResponseDto customerResponseDto = customerService.getCustomerDetails(customerId);
            return new ResponseEntity<>(customerResponseDto,HttpStatus.OK);
        }catch (ClassNotFoundException e){
            CustomerResponseDto customerResponseDto = new CustomerResponseDto();
            customerResponseDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(customerResponseDto,HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/all")
    public List<CustomerResponseDto> getAllCustomers(){

        return customerService.getAllCustomers();
    }
    @GetMapping("/getbyemail")
    public List<CustomerResponseDto> getCustomerByEmailId(@RequestParam String email){
            return  customerService.getCustomerByEmailId(email);
    }

}
