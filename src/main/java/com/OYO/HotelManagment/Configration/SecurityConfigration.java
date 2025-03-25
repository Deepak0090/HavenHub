package com.OYO.HotelManagment.Configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfigration{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf ->csrf.disable())
                .authorizeHttpRequests((authorizeRequest) ->
                authorizeRequest
                        .requestMatchers("/api/v1/hotels/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/customers/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/api/v1/bookings/cancel/**").hasRole("USER")
                        .requestMatchers("/api/v1/bookings/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/api/v1/rooms/**").hasRole("ADMIN")

                )
                .httpBasic(withDefaults()); // Enable HTTP Basic Authentication
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
       UserDetails user1 = User.builder().username("deepak").password(getCoder().encode("deepak@123")).roles("USER").build();
       UserDetails user2 = User.builder().username("ash").password(getCoder().encode("ash@123")).roles("ADMIN").build();

        return new InMemoryUserDetailsManager(user1,user2);
    }

    @Bean
    public PasswordEncoder getCoder(){
        return  new BCryptPasswordEncoder();
    }
}
