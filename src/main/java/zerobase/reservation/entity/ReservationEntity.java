package zerobase.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.reservation.dto.ReservationDto;
import zerobase.reservation.type.ReservationStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity(name = "Reservation")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@Setter
public class ReservationEntity implements ProjectEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customerEntity;
    
    @Column(nullable = false)
    private Integer headCount;
    
    @Column(nullable = false)
    private boolean visited;
    
    @Column(nullable = false)
    private ReservationStatus reservationStatus;
    
    @Column(nullable = false)
    private LocalDateTime reservedAt;
    
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public static ReservationEntity from(
            ReservationDto.Request form, CustomerEntity customerEntity,
            RestaurantEntity restaurantEntity) {
        
        return ReservationEntity.builder()
                .restaurantEntity(restaurantEntity)
                .customerEntity(customerEntity)
                .headCount(form.getHeadCount())
                .visited(false)
                .reservationStatus(ReservationStatus.WAIT)
                .reservedAt(form.getReservedAt())
                .build();
    }
}
