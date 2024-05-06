package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.entity.CustomerEntity;
import zerobase.reservation.entity.ReservationEntity;
import zerobase.reservation.entity.RestaurantEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,
        Long> {
    
    Optional<List<ReservationEntity>> findByRestaurantEntityOrderByReservedAt(
            RestaurantEntity restaurantEntity);
    
    List<ReservationEntity> findByReservedAtBefore(LocalDateTime localDateTime);
    
    Optional<ReservationEntity> findFirstByCustomerEntityAndRestaurantEntityOrderByReservedAtDesc(
            CustomerEntity customerEntity, RestaurantEntity restaurantEntity);
}