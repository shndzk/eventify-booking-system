package app.event.controller;


import event.app.api.UserApi;
import event.app.dto.NotificationPreferences;
import app.event.security.CurrentUserService;
import app.event.service.NotificationService;
import app.event.service.TelegramLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController implements UserApi {

    private final NotificationService notifService;
    private final TelegramLinkService telegramService;
    private final CurrentUserService currentUser;

    @Override
    public ResponseEntity<NotificationPreferences> userNotificationsGet() {
        return ResponseEntity.ok(notifService.get(currentUser.currentUserId()));
    }

    @Override
    public ResponseEntity<NotificationPreferences> userNotificationsPut(NotificationPreferences dto) {
        return ResponseEntity.ok(notifService.update(currentUser.currentUserId(), dto));
    }

    @Override
    public ResponseEntity<Void> userNotificationsDelete() {
        notifService.delete(currentUser.currentUserId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<String> userTelegramLinkPost() {
        String code = telegramService.createLinkCode(currentUser.currentUserId());
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(code);
    }
}

