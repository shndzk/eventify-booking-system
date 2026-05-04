package app.event.service.impl;


import event.app.dto.NotificationPreferences;
import app.event.entity.NotificationPreferencesEntity;
import app.event.exceptions.NotFoundException;
import app.event.mapper.NotificationMapper;
import app.event.repository.NotificationPreferencesRepository;
import app.event.repository.UserRepository;
import app.event.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPreferencesRepository repo;
    private final UserRepository userRepo;
    private final NotificationMapper mapper;

    @Override @Transactional(readOnly = true)
    public NotificationPreferences get(Long userId) {
        return mapper.toDto(repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Настройки уведомлений не найдены")));
    }

    @Override @Transactional
    public NotificationPreferences update(Long userId, NotificationPreferences dto) {
        NotificationPreferencesEntity e = repo.findByUserId(userId).orElseGet(() -> {
            var u = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
            return NotificationPreferencesEntity.builder().user(u)
                    .notifyNewEvents(true).notifyUpcoming(true).notifyBeforeHours(24).build();
        });
        mapper.update(e, dto);
        return mapper.toDto(repo.save(e));
    }

    @Override @Transactional
    public void delete(Long userId) { repo.deleteByUserId(userId); }
}

