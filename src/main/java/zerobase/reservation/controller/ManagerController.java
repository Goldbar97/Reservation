package zerobase.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reservation.dto.RestaurantForm;
import zerobase.reservation.dto.SignInForm;
import zerobase.reservation.dto.SignUpForm;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.service.ManagerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {
    
    private final ManagerService managerService;
    private final TokenProvider tokenProvider;
    
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<Object> createRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody RestaurantForm.Request form) {
        
        RestaurantForm.Response saved = managerService.createRestaurant(
                header, form);
        
        return ResponseEntity.ok(saved);
    }
    
    @PostMapping("/signin")
    public ResponseEntity<Object> signInManager(
            @RequestBody SignInForm.Request form) {
        
        SignInForm.Response signedIn = managerService.signInManager(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpManager(
            @RequestBody SignUpForm.Request form) {
        
        SignUpForm.Response saved = managerService.signUpManager(form);
        
        return ResponseEntity.ok(saved);
    }
}
