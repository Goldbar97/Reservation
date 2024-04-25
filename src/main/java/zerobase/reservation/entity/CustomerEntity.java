package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zerobase.reservation.dto.SignUpDto;
import zerobase.reservation.type.UserRole;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Entity(name = "Customer")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Setter
public class CustomerEntity implements UserDetails, ProjectEntity {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> role;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static CustomerEntity from(SignUpDto.Request form) {
        
        return CustomerEntity.builder()
                .email(form.getEmail())
                .name(form.getName())
                .role(new ArrayList<>(
                        Arrays.asList(UserRole.ROLE_CUSTOMER.toString())))
                .password(form.getPassword())
                .phoneNumber(form.getPhoneNumber())
                .build();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        return role.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getUsername() {
        
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        
        return false;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        
        return false;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        
        return false;
    }
    
    @Override
    public boolean isEnabled() {
        
        return false;
    }
}