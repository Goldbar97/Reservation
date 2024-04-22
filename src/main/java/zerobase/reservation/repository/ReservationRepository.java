package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.entity.ReservationEntity;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity,
        Long> {
    
}
