package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reservation.dto.*;
import zerobase.reservation.entity.CustomerEntity;
import zerobase.reservation.entity.ReservationEntity;
import zerobase.reservation.entity.RestaurantEntity;
import zerobase.reservation.entity.ReviewEntity;
import zerobase.reservation.exception.CustomException;
import zerobase.reservation.exception.ErrorCode;
import zerobase.reservation.repository.*;
import zerobase.reservation.security.JwtTokenUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {
    
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    
    public List<RestaurantsProjection> getRestaurants() {
        
        return restaurantRepository.findBy();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        
        return customerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username));
    }
    
    @Transactional
    public ReviewForm.Response rateRestaurant(
            String header, ReviewForm.Request form, Long id) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        ReviewEntity saved = reviewRepository.save(ReviewEntity.from(
                form, customerEntity, restaurantEntity));
        
        return ReviewForm.Response.builder()
                .reviewId(saved.getId())
                .build();
    }
    
    @Transactional
    public ReservationForm.Response reserveRestaurant(
            String header, ReservationForm.Request form, Long id) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        ReservationEntity saved = reservationRepository.save(
                ReservationEntity.from(form, customerEntity, restaurantEntity));
        
        return ReservationForm.Response.builder()
                .reservationId(saved.getId())
                .customerId(customerEntity.getId())
                .reservedAt(saved.getReservedAt())
                .restaurantId(saved.getRestaurantEntity().getId())
                .build();
    }
    
    public SignInForm.Response signInCustomer(SignInForm.Request form) {
        
        CustomerEntity customerEntity = customerRepository.findByEmail(
                        form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        
        if (!passwordEncoder.matches(
                form.getPassword(), customerEntity.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_LOGIN);
        }
        
        return SignInForm.Response.builder()
                .email(customerEntity.getEmail())
                .role(customerEntity.getRole())
                .build();
    }
    
    @Transactional
    public SignUpForm.Response signUpCustomer(SignUpForm.Request form) {
        
        boolean exists = customerRepository.existsByEmail(form.getEmail());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        
        CustomerEntity saved = customerRepository.save(
                CustomerEntity.from(form));
        
        return SignUpForm.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }
}
