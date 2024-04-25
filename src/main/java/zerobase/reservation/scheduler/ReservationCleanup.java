package zerobase.reservation.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.reservation.entity.ReservationEntity;
import zerobase.reservation.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationCleanup {
    
    private final ReservationRepository reservationRepository;
    
    @Scheduled(cron = "0 0 0/1 * * *")
    public void cleanupOldReservations() {
        
        LocalDateTime currentTime = LocalDateTime.now();
        
        List<ReservationEntity> reservationEntities =
                reservationRepository.findByReservedAtBefore(currentTime);
        
        reservationEntities.stream().filter(entity -> !entity.isVisited())
                .forEach(reservationRepository::delete);
    }
}
