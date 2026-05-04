package app.event.repository;


import app.event.entity.NotificationPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferencesEntity, Long> {
    Optional<NotificationPreferencesEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

