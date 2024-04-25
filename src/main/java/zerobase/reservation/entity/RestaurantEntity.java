package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.RestaurantDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Entity(name = "Restaurant")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Setter
public class RestaurantEntity implements ProjectEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;
    
    @OneToMany(mappedBy = "restaurantEntity", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntity;
    
    @Column(unique = true, nullable = false)
    private String businessNumber;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static RestaurantEntity from(
            RestaurantDto.Request form, ManagerEntity managerEntity,
            List<ReviewEntity> reviewEntities) {
        
        return RestaurantEntity.builder()
                .managerEntity(managerEntity)
                .reviewEntity(reviewEntities)
                .businessNumber(form.getBusinessNumber())
                .name(form.getName())
                .location(form.getLocation())
                .capacity(form.getCapacity())
                .description(form.getDescription())
                .phoneNumber(form.getPhoneNumber())
                .build();
    }
}