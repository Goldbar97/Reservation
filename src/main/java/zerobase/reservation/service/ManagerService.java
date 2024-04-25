package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reservation.dto.*;
import zerobase.reservation.entity.ManagerEntity;
import zerobase.reservation.entity.ReservationEntity;
import zerobase.reservation.entity.RestaurantEntity;
import zerobase.reservation.exception.CustomException;
import zerobase.reservation.exception.ErrorCode;
import zerobase.reservation.repository.ManagerRepository;
import zerobase.reservation.repository.ReservationRepository;
import zerobase.reservation.repository.RestaurantRepository;
import zerobase.reservation.repository.ReviewRepository;
import zerobase.reservation.security.JwtTokenUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {
    
    private final ManagerRepository managerRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public RestaurantDto.Response createRestaurant(
            String header, RestaurantDto.Request form) {
        
        boolean exists = restaurantRepository.existsByBusinessNumber(
                form.getBusinessNumber());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_RESTAURANT);
        }
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        ManagerEntity managerEntity = managerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity saved = restaurantRepository.save(
                RestaurantEntity.from(form, managerEntity, new ArrayList<>()));
        
        return RestaurantDto.Response.builder()
                .restaurantId(saved.getId())
                .build();
    }
    
    public ReservationDto.Response decideReservation(
            String header, ReservationStatusDto status, Long restaurantId,
            Long reservationId) {
        
        verifyManagerToRestaurant(header, restaurantId);
        ReservationEntity reservationEntity = verifyReservationToRestaurant(
                reservationId, restaurantId);
        
        reservationEntity.setReservationStatus(status.getReservationStatus());
        
        ReservationEntity edited = reservationRepository.save(
                reservationEntity);
        
        return ReservationDto.Response.builder()
                .reservationId(edited.getId())
                .customerId(edited.getCustomerEntity().getId())
                .restaurantId(edited.getRestaurantEntity().getId())
                .headCount(edited.getHeadCount())
                .reservedAt(edited.getReservedAt())
                .build();
    }
    
    private ReservationEntity verifyReservationToRestaurant(Long reservationId, Long restaurantId) {
        
        ReservationEntity reservationEntity = reservationRepository.findById(
                reservationId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_SUCH_RESERVATION));
        
        if (!reservationEntity.getRestaurantEntity().getId().equals(
                restaurantId)) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        return reservationEntity;
    }
    
    public List<ReservationDto.Response> getReservations(
            String header, Long restaurantId) {
        
        List<Object> entities = verifyManagerToRestaurant(header, restaurantId);
        
        RestaurantEntity restaurantEntity =
                (RestaurantEntity) entities.getLast();
        
        List<ReservationEntity> list =
                reservationRepository.findByRestaurantEntityOrderByReservedAt(
                                restaurantEntity)
                        .orElseThrow(() -> new CustomException(
                                ErrorCode.UNAUTHORIZED));
        
        return list.stream()
                .map(restaurant -> ReservationDto.Response.builder()
                        .reservationId(restaurant.getId())
                        .customerId(restaurant.getCustomerEntity().getId())
                        .restaurantId(restaurant.getRestaurantEntity().getId())
                        .headCount(restaurant.getHeadCount())
                        .reservedAt(restaurant.getReservedAt())
                        .build()).toList();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        
        return managerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username));
    }
    
    public SignInDto.Response signIn(SignInDto.Request form) {
        
        ManagerEntity managerEntity = managerRepository.findByEmail(
                        form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        
        if (!passwordEncoder.matches(
                form.getPassword(), managerEntity.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_LOGIN);
        }
        
        return SignInDto.Response.builder()
                .email(managerEntity.getEmail())
                .role(managerEntity.getRole())
                .build();
    }
    
    @Transactional
    public SignUpDto.Response signUp(SignUpDto.Request form) {
        
        boolean exists = managerRepository.existsByEmail(form.getEmail());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        
        ManagerEntity saved = managerRepository.save(ManagerEntity.from(form));
        
        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }
    
    /**
     * This verifies the manager is the owner of the restaurant.
     *
     * @param header       retrieved from header "Authorization"
     * @param restaurantId primary key in table "Restaurant"
     * @return List that contains ManagerEntity at index 0 and
     * RestaurantEntity at index 1
     */
    private List<Object> verifyManagerToRestaurant(
            String header, Long restaurantId) {
        
        String token = jwtTokenUtil.getToken(header);
        String email = jwtTokenUtil.getEmail(token);
        
        ManagerEntity managerEntity = managerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_USER));
        RestaurantEntity restaurantEntity = restaurantRepository.findById(
                        restaurantId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NO_SUCH_RESTAURANT));
        
        if (!managerEntity.getId().equals(
                restaurantEntity.getManagerEntity().getId())) {
            
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        return List.of(managerEntity, restaurantEntity);
    }
}
