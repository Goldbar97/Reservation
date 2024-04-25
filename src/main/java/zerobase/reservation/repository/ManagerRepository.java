package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.entity.ManagerEntity;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerEntity,
        Long> {
    
    boolean existsByEmail(String email);
    
    Optional<ManagerEntity> findByEmail(String email);
    
}
