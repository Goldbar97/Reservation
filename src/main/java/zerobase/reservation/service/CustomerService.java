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
import zerobase.reservation.type.ReservationStatus;

import java.time.Duration;
import java.time.LocalDateTime;
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
    
    /**
     * 손님, 예약, 시간 확인 후 방문 확인
     */
    @Transactional
    public ReservationStatusDto.Response confirmReservation(
            String header, Long reservationId) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        ReservationEntity reservationEntity = reservationRepository.findById(
                reservationId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_SUCH_RESERVATION));
        
        if (!customerEntity.getId()
                .equals(reservationEntity.getCustomerEntity().getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime reservedAt = reservationEntity.getReservedAt();
        long minutes = Duration.between(current, reservedAt).toMinutes();
        
        if (!(0 <= minutes && minutes <= 10)) {
            throw new CustomException(ErrorCode.EARLY_CONFIRMATION);
        }
        
        ReservationStatus status = reservationEntity.getReservationStatus();
        
        switch (status) {
            case ReservationStatus.WAIT ->
                    throw new CustomException(ErrorCode.WAIT_RESERVATION);
            
            case ReservationStatus.REJECTED ->
                    throw new CustomException(ErrorCode.REJECTED_RESERVATION);
        }
        
        reservationEntity.setVisited(true);
        
        ReservationEntity edited = reservationRepository.save(
                reservationEntity);
        
        return ReservationStatusDto.Response.builder()
                .status(edited.getReservationStatus())
                .visited(edited.isVisited())
                .reservationId(edited.getId())
                .customerId(edited.getCustomerEntity().getId())
                .restaurantId(edited.getRestaurantEntity().getId())
                .headCount(edited.getHeadCount())
                .reservedAt(edited.getReservedAt())
                .build();
    }
    
    /**
     * 손님, 매장, 예약 확인 후 리뷰 작성
     */
    @Transactional
    public ReviewDto.Response createReview(
            String header, ReviewDto.Request form, Long restaurantId) {
        
        List<Object> entities = getCustomerAndRestaurant(header, restaurantId);
        CustomerEntity customerEntity = (CustomerEntity) entities.get(0);
        RestaurantEntity restaurantEntity = (RestaurantEntity) entities.get(1);
        ReservationEntity reservationEntity =
                reservationRepository.findFirstByCustomerEntityAndRestaurantEntityOrderByReservedAtDesc(
                                customerEntity,
                                restaurantEntity)
                        .orElseThrow(() -> new CustomException(
                                ErrorCode.NO_SUCH_RESERVATION));
        
        if (!reservationEntity.isVisited()) {
            throw new CustomException(ErrorCode.WRONG_REVIEW);
        }
        
        ReviewEntity saved = reviewRepository.save(ReviewEntity.from(
                form, customerEntity, restaurantEntity));
        
        return ReviewDto.Response.builder()
                .reviewId(saved.getId())
                .build();
    }
    
    /**
     * 손님, 매장, 리뷰 확인 후 리뷰 삭제
     */
    @Transactional
    public boolean deleteReview(
            String header, Long reviewId, Long restaurantId) {
        
        List<Object> entities = getCustomerAndRestaurant(header, restaurantId);
        CustomerEntity customerEntity = (CustomerEntity) entities.get(0);
        RestaurantEntity restaurantEntity = (RestaurantEntity) entities.get(1);
        ReviewEntity reviewEntity = reviewRepository.findById(
                reviewId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_SUCH_REVIEW));
        
        if (!customerEntity.getId()
                .equals(reviewEntity.getCustomerEntity().getId())) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else if (!restaurantEntity.getId()
                .equals(reviewEntity.getRestaurantEntity().getId())) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        reviewRepository.delete(reviewEntity);
        
        return true;
    }
    
    /**
     * 손님, 매장, 리뷰 확인 후 수정 부분만 리뷰 수정
     */
    @Transactional
    public ReviewDto.Response editReview(
            String header, ReviewDto.Request form, Long reviewId,
            Long restaurantId) {
        
        List<Object> entities = getCustomerAndRestaurant(header, restaurantId);
        CustomerEntity customerEntity = (CustomerEntity) entities.get(0);
        RestaurantEntity restaurantEntity = (RestaurantEntity) entities.get(1);
        ReviewEntity reviewEntity = reviewRepository.findById(
                reviewId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_SUCH_REVIEW));
        
        if (!customerEntity.getId()
                .equals(reviewEntity.getCustomerEntity().getId())) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else if (!restaurantEntity.getId()
                .equals(reviewEntity.getRestaurantEntity().getId())) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        if (form.getRate() != null) {
            reviewEntity.setRate(form.getRate());
        }
        
        if (form.getDescription() != null) {
            reviewEntity.setDescription(form.getDescription());
        }
        
        ReviewEntity edited = reviewRepository.save(reviewEntity);
        
        return ReviewDto.Response.builder()
                .reviewId(edited.getId())
                .build();
    }
    
    /**
     * 매장 조회
     */
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
    
    /**
     * 손님, 매장 확인 후 예약
     */
    @Transactional
    public ReservationDto.Response reserveRestaurant(
            String header, ReservationDto.Request form, Long restaurantId) {
        
        List<Object> entities = getCustomerAndRestaurant(header, restaurantId);
        CustomerEntity customerEntity = (CustomerEntity) entities.get(0);
        RestaurantEntity restaurantEntity = (RestaurantEntity) entities.get(1);
        
        ReservationEntity saved = reservationRepository.save(
                ReservationEntity.from(form, customerEntity, restaurantEntity));
        
        return ReservationDto.Response.builder()
                .reservationId(saved.getId())
                .customerId(customerEntity.getId())
                .restaurantId(restaurantEntity.getId())
                .headCount(saved.getHeadCount())
                .reservedAt(saved.getReservedAt())
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
    
    /**
     * 토큰과 매장 id 확인
     */
    private List<Object> getCustomerAndRestaurant(
            String header, Long restaurantId) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        CustomerEntity customerEntity = customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(
                        restaurantId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        return List.of(customerEntity, restaurantEntity);
    }
}
