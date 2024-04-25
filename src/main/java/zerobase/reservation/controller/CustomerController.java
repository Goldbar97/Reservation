package zerobase.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reservation.dto.*;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.service.CustomerService;

import java.util.List;

@RequestMapping("/customer")
@RequiredArgsConstructor
@RestController
public class CustomerController {
    
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;
    
    @GetMapping("/restaurants")
    public ResponseEntity<Object> getRestaurants() {
        
        List<RestaurantDto.Response> restaurants =
                customerService.getRestaurants();
        
        return ResponseEntity.ok(restaurants);
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/rate/{id}")
    public ResponseEntity<Object> rateRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody ReviewDto.Request form,
            @PathVariable Long id) {
        
        ReviewDto.Response saved = customerService.rateRestaurant(
                header, form, id);
        
        return ResponseEntity.ok(saved);
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/reservation/{id}")
    public ResponseEntity<Object> reserveRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody ReservationDto.Request form,
            @PathVariable Long id) {
        
        ReservationDto.Response saved = customerService.reserveRestaurant(
                header, form, id);
        
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/signin")
    public ResponseEntity<Object> signInCustomer(
            @RequestBody SignInDto.Request form) {
        
        SignInDto.Response signedIn = customerService.signIn(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpCustomer(
            @RequestBody SignUpDto.Request form) {
        
        SignUpDto.Response signedUp = customerService.signUp(form);
        
        return ResponseEntity.ok(signedUp);
    }
}
