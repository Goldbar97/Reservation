package zerobase.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.reservation.entity.CustomerEntity;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity,
        Long> {
    
    boolean existsByEmail(String email);
    
    Optional<CustomerEntity> findByEmail(String email);
}