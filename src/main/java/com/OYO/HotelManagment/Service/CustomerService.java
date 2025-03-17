package com.OYO.HotelManagment.Service;

import com.OYO.HotelManagment.DTO.Request.CustomerRequestDto;
import com.OYO.HotelManagment.DTO.Response.CustomerResponseDto;
import com.OYO.HotelManagment.Exception.CustomerNotFoundException;
import com.OYO.HotelManagment.Exception.DuplicateEmailException;
//import com.OYO.HotelManagment.Model.Aadhar;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    BookingRepo bookingRepo;

    public CustomerResponseDto createCustomers(CustomerRequestDto customerRequestDto) throws DuplicateEmailException {
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
        customer.setId(customerRequestDto.getId());
        customer.setStatus(customer.getStatus());
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
               List<Customer> customers= customerRepo.findByEmail(email);
               List<CustomerResponseDto> customerResponseDtos = new ArrayList<>();
               for (Customer customer : customers){
                   customerResponseDtos.add(converCustomerResponseDto(customer));
               }
               return customerResponseDtos;
    }

    public Customer getCustomerById(Integer customerID) throws CustomerNotFoundException {

        return  customerRepo.findById(customerID)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found"));
    }
}
