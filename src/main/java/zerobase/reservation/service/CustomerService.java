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
import zerobase.reservation.repository.CustomerRepository;
import zerobase.reservation.repository.ReservationRepository;
import zerobase.reservation.repository.RestaurantRepository;
import zerobase.reservation.repository.ReviewRepository;
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
    
    public List<RestaurantDto.Response> getRestaurants() {
        
        List<RestaurantEntity> restaurantEntities =
                restaurantRepository.findAll();
        
        return restaurantEntities.stream()
                .map(i -> RestaurantDto.Response.builder()
                        .restaurantId(i.getId())
                        .name(i.getName())
                        .location(i.getLocation())
                        .capacity(i.getCapacity())
                        .phoneNumber(i.getPhoneNumber())
                        .description(i.getDescription())
                        .build())
                .toList();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        
        return customerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username));
    }
    
    @Transactional
    public ReviewDto.Response rateRestaurant(
            String header, ReviewDto.Request form, Long id) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        ReviewEntity saved = reviewRepository.save(ReviewEntity.from(
                form, customerEntity, restaurantEntity));
        
        return ReviewDto.Response.builder()
                .reviewId(saved.getId())
                .build();
    }
    
    @Transactional
    public ReservationDto.Response reserveRestaurant(
            String header, ReservationDto.Request form, Long id) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        ReservationEntity saved = reservationRepository.save(
                ReservationEntity.from(form, customerEntity, restaurantEntity));
        
        return ReservationDto.Response.builder()
                .reservationId(saved.getId())
                .customerId(customerEntity.getId())
                .reservedAt(saved.getReservedAt())
                .restaurantId(saved.getRestaurantEntity().getId())
                .headCount(saved.getHeadCount())
                .build();
    }
    
    public SignInDto.Response signIn(SignInDto.Request form) {
        
        CustomerEntity customerEntity = customerRepository.findByEmail(
                        form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        
        if (!passwordEncoder.matches(
                form.getPassword(), customerEntity.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_LOGIN);
        }
        
        return SignInDto.Response.builder()
                .email(customerEntity.getEmail())
                .role(customerEntity.getRole())
                .build();
    }
    
    @Transactional
    public SignUpDto.Response signUp(SignUpDto.Request form) {
        
        boolean exists = customerRepository.existsByEmail(form.getEmail());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        
        CustomerEntity saved = customerRepository.save(
                CustomerEntity.from(form));
        
        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }
}
