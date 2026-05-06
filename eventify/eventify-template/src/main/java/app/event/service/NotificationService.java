package app.event.service;


import event.app.dto.NotificationPreferences;

public interface NotificationService {
    NotificationPreferences get(Long userId);
    NotificationPreferences update(Long userId, NotificationPreferences dto);
    void delete(Long userId);
}

