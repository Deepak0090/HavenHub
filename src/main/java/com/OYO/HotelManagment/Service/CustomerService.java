package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Request.CustomerRequestDto;
import com.OYO.HotelManagment.DTO.Response.CustomerResponseDto;
import com.OYO.HotelManagment.Exception.CustomerNotFoundException;
import com.OYO.HotelManagment.Exception.DuplicateEmailException;
//import com.OYO.HotelManagment.Model.Aadhar;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomerResponseDto createCustomers(CustomerRequestDto customerRequestDto) throws DuplicateEmailException {

        Optional<Customer> existingCustomer = customerRepo.findByEmail(customerRequestDto.getEmail());
        if (!existingCustomer.isEmpty()){
            throw new DuplicateEmailException("This email already exists. Please use another email.");
        }
        try {
            Customer customer = convertCustomerRequestDto(customerRequestDto);
//            Aadhar aadhar = customerRequestDto.getAadhar();
//            aadhar.setCustomer(customer);
            customerRepo.save(customer);
            CustomerResponseDto customerResponseDto = new CustomerResponseDto();
            customerResponseDto.setName(customer.getName());
            customerResponseDto.setEmail(customer.getEmail());
            customerResponseDto.setStatus("You are Successfully Created");
            customerResponseDto.setContactNumber(customer.getContactNumber());

            return customerResponseDto;
        }catch (DataIntegrityViolationException e){
            throw new DuplicateEmailException("this email is Already Exist Please Use Another Email");
        }
    }

    private Customer convertCustomerRequestDto(CustomerRequestDto customerRequestDto) {

        Customer customer = new Customer();
        customer.setEmail(customerRequestDto.getEmail());
        customer.setName(customerRequestDto.getName());
        customer.setContactNumber(customerRequestDto.getContactNumber());
//        customer.setAadhar(customerRequestDto.getAadhar());
        customer.setPassword(passwordEncoder.encode(customerRequestDto.getPassword()));
        customer.setId(customerRequestDto.getId());
        customer.setStatus("ACTIVE");
        customer.setBookings(List.of());
        customer.setCreatedAt(LocalDateTime.now());

        return customer;
    }
    public CustomerResponseDto getCustomerDetails(Integer customerId) throws ClassNotFoundException{
        Optional<Customer> customer = customerRepo.findById(customerId);

        if (!customer.isPresent()){
            throw new ClassNotFoundException("Customer with this customerId: "+customerId+" does not present");
        }
        return converCustomerResponseDto(customer.get());
    }

    public List<CustomerResponseDto> getAllCustomers(){
        List<Customer> customers = customerRepo.findAll();
        List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
        for (Customer customer : customers){
            customerResponseDtos.add(converCustomerResponseDto(customer));
        }
        return customerResponseDtos;
    }

    private CustomerResponseDto converCustomerResponseDto(Customer customer){

        return CustomerResponseDto.builder().name(customer.getName())
                .email(customer.getEmail())
                .status(customer.getStatus())
                .contactNumber(customer.getContactNumber())
                .build();
    }

    public List<CustomerResponseDto> getCustomerByEmailId(String email) {
               Optional<Customer> customers= customerRepo.findByEmail(email);
               List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
               customers.ifPresent(customer -> customerResponseDtos.add(converCustomerResponseDto(customer)));
               return customerResponseDtos;
    }

    public Customer getCustomerById(Integer customerID) throws CustomerNotFoundException {

        return  customerRepo.findById(customerID)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found"));
    }

    public String deleteCustomerById(Integer id) throws CustomerNotFoundException {
         Optional<Customer> exist = customerRepo.findById(id);

         if(exist.isEmpty()){
             throw new CustomerNotFoundException("No Customer Exist For this ID: "+id);
         }
         customerRepo.deleteById(id);
        return "deleted";
    }
}
