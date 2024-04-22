package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.ReviewForm;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity(name = "Review")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class ReviewEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;
    
    private String name;
    private String description;
    private Integer rate;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static ReviewEntity from(
            ReviewForm.Request form, CustomerEntity customerEntity,
            RestaurantEntity restaurantEntity) {
        
        return ReviewEntity.builder()
                .description(form.getDescription())
                .rate(form.getRate())
                .name(customerEntity.getName())
                .customerEntity(customerEntity)
                .restaurantEntity(restaurantEntity)
                .build();
    }
}

