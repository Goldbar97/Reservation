package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.ReservationForm;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity(name = "Reservation")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class ReservationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;
    
    private Integer headCount;
    private Boolean visited;
    
    private LocalDateTime reservedAt;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static ReservationEntity from(
            ReservationForm.Request form, CustomerEntity customerEntity,
            RestaurantEntity restaurantEntity) {
        
        return ReservationEntity.builder()
                .customerEntity(customerEntity)
                .restaurantEntity(restaurantEntity)
                .headCount(form.getHeadCount())
                .reservedAt(form.getReservedAt())
                .build();
    }
}
