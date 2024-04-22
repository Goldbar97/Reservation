package zerobase.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.reservation.dto.RestaurantForm;
import zerobase.reservation.dto.SignInForm;
import zerobase.reservation.dto.SignUpForm;
import zerobase.reservation.entity.ManagerEntity;
import zerobase.reservation.entity.RestaurantEntity;
import zerobase.reservation.exception.CustomException;
import zerobase.reservation.exception.ErrorCode;
import zerobase.reservation.repository.ManagerRepository;
import zerobase.reservation.repository.RestaurantRepository;
import zerobase.reservation.repository.ReviewRepository;
import zerobase.reservation.security.JwtTokenUtil;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {
    
    private final ManagerRepository managerRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    
    private final String TOKEN_PREFIX = "Bearer ";
    
    @Transactional
    public RestaurantForm.Response createRestaurant(
            String header, RestaurantForm.Request form) {
        
        boolean exists = restaurantRepository.existsByBusinessNumber(
                form.getBusinessNumber());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_RESTAURANT);
        }
        
        String token = header.substring(TOKEN_PREFIX.length());
        String email = jwtTokenUtil.getEmail(token);
        
        ManagerEntity managerEntity = managerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        RestaurantEntity saved = restaurantRepository.save(
                RestaurantEntity.from(form, managerEntity, new ArrayList<>()));
        
        return RestaurantForm.Response.builder()
                .restaurantId(saved.getId())
                .build();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        
        return managerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username));
    }
    
    public SignInForm.Response signInManager(SignInForm.Request form) {
        
        ManagerEntity managerEntity = managerRepository.findByEmail(
                        form.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_SUCH_USER));
        
        if (!passwordEncoder.matches(
                form.getPassword(), managerEntity.getPassword())) {
            throw new CustomException(ErrorCode.WRONG_LOGIN);
        }
        
        return SignInForm.Response.builder()
                .email(managerEntity.getEmail())
                .role(managerEntity.getRole())
                .build();
    }
    
    @Transactional
    public SignUpForm.Response signUpManager(SignUpForm.Request form) {
        
        boolean exists = managerRepository.existsByEmail(form.getEmail());
        
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        
        ManagerEntity saved = managerRepository.save(ManagerEntity.from(form));
        
        return SignUpForm.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }
}
