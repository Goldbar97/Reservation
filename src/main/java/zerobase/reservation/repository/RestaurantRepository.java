package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.entity.RestaurantEntity;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity,
        Long> {
    
    boolean existsByBusinessNumber(String businessNumber);
    
    List<RestaurantsProjection> findBy();
}