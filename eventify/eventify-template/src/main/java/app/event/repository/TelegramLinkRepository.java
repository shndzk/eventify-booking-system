package app.event.repository;


import app.event.entity.TelegramLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TelegramLinkRepository extends JpaRepository<TelegramLinkEntity, Long> {
    Optional<TelegramLinkEntity> findByUserIdAndLinkedFalse(Long userId);

    Optional<TelegramLinkEntity> findByLinkCode(String linkCode);

}

