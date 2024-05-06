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
    private final String AUTHORIZATION = "Authorization";
    private final String RESERVATION_ID = "reservationid";
    private final String RESTAURANT_ID = "restaurantid";
    private final String REVIEW_ID = "reviewid";
    
    /**
     * 점주의 토큰을 확인하고 매장을 추가합니다.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<Object> createRestaurant(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestBody RestaurantDto.Request form) {
        
        RestaurantDto.Response saved = managerService.createRestaurant(
                header, form);
        
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 점주가 예약을 결정합니다.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/reservation")
    public ResponseEntity<Object> decideReservation(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestBody ReservationStatusDto.Request status,
            @RequestParam(RESTAURANT_ID) Long restaurantId,
            @RequestParam(RESERVATION_ID) Long reservationId) {
        
        ReservationStatusDto.Response edited = managerService.decideReservation(
                header, status, restaurantId, reservationId);
        
        return ResponseEntity.ok(edited);
    }
    
    /**
     * 리뷰를 삭제합니다.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/rate")
    public ResponseEntity<Object> deleteReview(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestParam(REVIEW_ID) Long reviewId,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        boolean deleted = managerService.deleteReview(
                header, reviewId, restaurantId);
        
        return ResponseEntity.ok(deleted);
    }
    
    /**
     * 매장의 예약을 조회합니다.
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/reservation")
    public ResponseEntity<Object> getReservations(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        List<ReservationDto.Response> list = managerService.getReservations(
                header, restaurantId);
        
        return ResponseEntity.ok(list);
    }
    
    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<Object> signInManager(
            @RequestBody SignInDto.Request form) {
        
        SignInDto.Response signedIn = managerService.signIn(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpManager(
            @RequestBody SignUpDto.Request form) {
        
        SignUpDto.Response saved = managerService.signUp(form);
        
        return ResponseEntity.ok(saved);
    }
}
