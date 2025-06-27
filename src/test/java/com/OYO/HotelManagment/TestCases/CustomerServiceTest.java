package com.OYO.HotelManagment.TestCases;

import com.OYO.HotelManagment.DTO.Request.CustomerRequestDto;
import com.OYO.HotelManagment.DTO.Response.CustomerResponseDto;
import com.OYO.HotelManagment.Exception.CustomerNotFoundException;
import com.OYO.HotelManagment.Exception.DuplicateEmailException;
import com.OYO.HotelManagment.Model.Customer;
import com.OYO.HotelManagment.Repository.BookingRepo;
import com.OYO.HotelManagment.Repository.CustomerRepo;
import com.OYO.HotelManagment.Service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    CustomerRepo customerRepo;

    @Mock
    BookingRepo bookingRepo;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCustomerSuccess() throws DuplicateEmailException {
        CustomerRequestDto requestDto = new CustomerRequestDto();
        requestDto.setName("Deepak Kumar");
        requestDto.setContactNumber("9625936408");
        requestDto.setEmail("deepak936408@gmail.com");

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1);
        savedCustomer.setName("Deepak Kumar");
        savedCustomer.setEmail("deepak936408@gmail.com");
        savedCustomer.setContactNumber("9625936408");
        savedCustomer.setStatus("Active");

        when(customerRepo.findByEmail("deepak936408@gmail.com")).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponseDto responseDto = customerService.createCustomers(requestDto);

        assertEquals("Deepak Kumar", responseDto.getName());
        assertEquals("deepak936408@gmail.com", responseDto.getEmail());
        assertEquals("You are Successfully Created", responseDto.getStatus());

    }

    @Test
    public void  testCreateCustomerDuplicateEmail(){

        CustomerRequestDto requestDto = new CustomerRequestDto();
        requestDto.setName("Naveen");
        requestDto.setEmail("Naveen@gmail.com");
        requestDto.setContactNumber("9389728924");

        Customer existingCustomer = new Customer();
        existingCustomer.setId(1);
        existingCustomer.setName("Naveen");
        existingCustomer.setEmail("Naveen@gmail.com");
        existingCustomer.setContactNumber("9389728924");

        when(customerRepo.findByEmail("Naveen@gmail.com")).thenReturn(Optional.of(existingCustomer));

        assertThrows(DuplicateEmailException.class,()->{
            customerService.createCustomers(requestDto);
        });
    }

    @Test
    public void testGetCustomerDetailsNotFound(){
        when(customerRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ClassNotFoundException.class, () -> customerService.getCustomerDetails(1));
    }

    @Test
    public void testGetAllCustomers(){
        Customer customer = new Customer();
        customer.setName("Akash");
        customer.setEmail("Akash@gmail.com");
        customer.setContactNumber("9899606115");
        customer.setStatus("Active");

        when(customerRepo.findAll()).thenReturn(List.of(customer));
        List<CustomerResponseDto> list = customerService.getAllCustomers();
        assertEquals(1, list.size());
        assertEquals("Akash", list.get(0).getName());
    }

    @Test
    public void testGetCustomerByEmailId(){
        Customer customer = new Customer();
        customer.setName("Jerry");
        customer.setEmail("Jerry@gmail.com");
        customer.setContactNumber("9278924629");
        customer.setStatus("Active");

        when(customerRepo.findByEmail("Dheeraj@gmail.com")).thenReturn(Optional.of(customer));
        List<CustomerResponseDto> customerResponseDtos = customerService.getCustomerByEmailId("Dheeraj@gmail.com");
        assertEquals(1, customerResponseDtos.size());
        assertEquals("Jerry",customerResponseDtos.get(0).getName());
    }

    @Test
    public void testGetCustomerByIdSuccess() throws CustomerNotFoundException{
        Customer customer = new Customer();
        customer.setId(1);
        when(customerRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, ()->customerService.getCustomerById(1));
    }
}
