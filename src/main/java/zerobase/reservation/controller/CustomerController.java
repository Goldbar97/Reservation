package zerobase.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reservation.dto.*;
import zerobase.reservation.repository.RestaurantsProjection;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.service.CustomerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {
    
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;
    
    @GetMapping("/restaurants")
    public ResponseEntity<Object> getRestaurants() {
        
        List<RestaurantsProjection> restaurants =
                customerService.getRestaurants();
        
        return ResponseEntity.ok(restaurants);
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/rate/{id}")
    public ResponseEntity<Object> rateRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody ReviewForm.Request form,
            @PathVariable Long id) {
        
        ReviewForm.Response saved = customerService.rateRestaurant(
                header, form, id);
        
        return ResponseEntity.ok(saved);
    }
    
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/reservation/{id}")
    public ResponseEntity<Object> reserveRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody ReservationForm.Request form,
            @PathVariable Long id) {
        
        ReservationForm.Response saved = customerService.reserveRestaurant(
                header, form, id);
        
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/signin")
    public ResponseEntity<Object> signInCustomer(
            @RequestBody SignInForm.Request form) {
        
        SignInForm.Response signedIn = customerService.signInCustomer(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpCustomer(
            @RequestBody SignUpForm.Request form) {
        
        SignUpForm.Response signedUp = customerService.signUpCustomer(form);
        
        return ResponseEntity.ok(signedUp);
    }
}
