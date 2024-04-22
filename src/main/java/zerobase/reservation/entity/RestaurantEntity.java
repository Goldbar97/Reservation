package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.RestaurantForm;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Entity(name = "Restaurant")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class RestaurantEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "manager_id")
    private ManagerEntity managerEntity;
    
//    @JoinColumn(name = "review_id")
    @OneToMany(mappedBy = "restaurantEntity")
    private List<ReviewEntity> reviewEntity;
    
    @Column(unique = true)
    private String businessNumber;
    
    private String name;
    private String location;
    private Integer capacity;
    private String description;
    private String phoneNumber;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static RestaurantEntity from(
            RestaurantForm.Request form, ManagerEntity managerEntity,
            List<ReviewEntity> reviewEntities) {
        
        return RestaurantEntity.builder()
                .name(form.getName())
                .location(form.getLocation())
                .capacity(form.getCapacity())
                .managerEntity(managerEntity)
                .phoneNumber(form.getPhoneNumber())
                .businessNumber(form.getBusinessNumber())
                .reviewEntity(reviewEntities)
                .description(form.getDescription())
                .build();
    }
}