package app.event.mapper;


import event.app.dto.NotificationPreferences;
import app.event.entity.NotificationPreferencesEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationPreferences toDto(NotificationPreferencesEntity e) {
        return new NotificationPreferences()
                .notifyNewEvents(e.isNotifyNewEvents())
                .notifyUpcoming(e.isNotifyUpcoming())
                .notifyBeforeHours(e.getNotifyBeforeHours());
    }

    public void update(NotificationPreferencesEntity target, NotificationPreferences dto) {
        if (dto.getNotifyNewEvents()  != null) target.setNotifyNewEvents(dto.getNotifyNewEvents());
        if (dto.getNotifyUpcoming()   != null) target.setNotifyUpcoming(dto.getNotifyUpcoming());
        if (dto.getNotifyBeforeHours()!= null) target.setNotifyBeforeHours(dto.getNotifyBeforeHours());
    }
}

