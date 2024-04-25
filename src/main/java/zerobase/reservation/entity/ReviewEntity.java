package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.ReviewDto;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity(name = "Review")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Setter
public class ReviewEntity implements ProjectEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private Integer rate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static ReviewEntity from(
            ReviewDto.Request form, CustomerEntity customerEntity,
            RestaurantEntity restaurantEntity) {
        
        return ReviewEntity.builder()
                .restaurantEntity(restaurantEntity)
                .customerEntity(customerEntity)
                .name(customerEntity.getName())
                .description(form.getDescription())
                .rate(form.getRate())
                .build();
    }
}

