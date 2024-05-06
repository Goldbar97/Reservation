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
    private final String AUTHORIZATION = "Authorization";
    private final String RESERVATION_ID = "reservationid";
    private final String RESTAURANT_ID = "restaurantid";
    private final String REVIEW_ID = "reviewid";
    
    /**
     * 예약 후 방문 확인
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/reservation")
    public ResponseEntity<Object> confirmReservation(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestParam(RESERVATION_ID) Long reservationId) {
        
        ReservationStatusDto.Response edited =
                customerService.confirmReservation(
                        header, reservationId);
        
        return ResponseEntity.ok(edited);
    }
    
    /**
     * 리뷰 작성
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/rate")
    public ResponseEntity<Object> createReview(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestBody ReviewDto.Request form,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        ReviewDto.Response saved = customerService.createReview(
                header, form, restaurantId);
        
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 리뷰 삭제
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("rate")
    public ResponseEntity<Object> deleteReview(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestParam(REVIEW_ID) Long reviewId,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        boolean deleted = customerService.deleteReview(
                header, reviewId, restaurantId);
        
        return ResponseEntity.ok(deleted);
    }
    
    /**
     * 리뷰 수정
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/rate")
    public ResponseEntity<Object> editReview(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestBody ReviewDto.Request form,
            @RequestParam(REVIEW_ID) Long reviewId,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        ReviewDto.Response edited = customerService.editReview(
                header, form, reviewId, restaurantId);
        
        return ResponseEntity.ok(edited);
    }
    
    /**
     * 매장 조회
     */
    @GetMapping("/restaurants")
    public ResponseEntity<Object> getRestaurants() {
        
        List<RestaurantDto.Response> restaurants =
                customerService.getRestaurants();
        
        return ResponseEntity.ok(restaurants);
    }
    
    /**
     * 매장 예약
     */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/reservation")
    public ResponseEntity<Object> reserveRestaurant(
            @RequestHeader(AUTHORIZATION) String header,
            @RequestBody ReservationDto.Request form,
            @RequestParam(RESTAURANT_ID) Long restaurantId) {
        
        ReservationDto.Response saved = customerService.reserveRestaurant(
                header, form, restaurantId);
        
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 로그인
     */
    @PostMapping("/signin")
    public ResponseEntity<Object> signInCustomer(
            @RequestBody SignInDto.Request form) {
        
        SignInDto.Response signedIn = customerService.signIn(form);
        
        String token = tokenProvider.generateToken(
                signedIn.getEmail(), signedIn.getRole());
        signedIn.setToken(token);
        
        return ResponseEntity.ok(signedIn);
    }
    
    /**
     * 회원 가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signUpCustomer(
            @RequestBody SignUpDto.Request form) {
        
        SignUpDto.Response signedUp = customerService.signUp(form);
        
        return ResponseEntity.ok(signedUp);
    }
}
