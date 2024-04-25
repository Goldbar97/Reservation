package zerobase.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.reservation.dto.*;
import zerobase.reservation.security.TokenProvider;
import zerobase.reservation.service.ManagerService;

import java.util.List;

@RequestMapping("/manager")
@RequiredArgsConstructor
@RestController
public class ManagerController {
    
    private final ManagerService managerService;
    private final TokenProvider tokenProvider;
    private final String HEADER_AUTH = "Authorization";
    
    @PutMapping("/reservation/")
    public ResponseEntity<Object> decideReservation(
            @RequestHeader(HEADER_AUTH) String header,
            @RequestBody ReservationStatusDto status,
            @RequestParam Long restaurantId,
            @RequestParam Long reservationId) {
        
        ReservationDto.Response edited = managerService.decideReservation(
                header, status, restaurantId, reservationId);
        
        return ResponseEntity.ok(edited);
    }
    
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<Object> createRestaurant(
            @RequestHeader("Authorization") String header,
            @RequestBody RestaurantDto.Request form) {
        
        RestaurantDto.Response saved = managerService.createRestaurant(
                header, form);
        
        return ResponseEntity.ok(saved);
    }
    
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/reservation/{restaurantId}")
    public ResponseEntity<Object> getReservations(
            @RequestHeader("Authorization") String header,
            @PathVariable Long restaurantId) {
        
        List<ReservationDto.Response> list = managerService.getReservations(
                header, restaurantId);
        
        return ResponseEntity.ok(list);
    }
    
    @PostMapping("/signin")
    public ResponseEntity<Object> signInManager(
            @RequestBody SignInDto.Request form) {
        
        SignInDto.Response signedIn = managerService.signIn(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpManager(
            @RequestBody SignUpDto.Request form) {
        
        SignUpDto.Response saved = managerService.signUp(form);
        
        return ResponseEntity.ok(saved);
    }
}
