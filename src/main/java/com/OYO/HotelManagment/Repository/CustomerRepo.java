package com.OYO.HotelManagment.Repository;

import com.OYO.HotelManagment.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Integer>{

    Optional<Customer> findById(Integer Id);

//    @Query(value = "select c from Customer c where c.email= :email",nativeQuery = false) // this is Hibernate Query
    @Query(value = "select * from customers where email_id = :email",nativeQuery = true) // we will always use this
    List<Customer> findByEmail(String email);
}
