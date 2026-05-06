package app.event.service.impl;


import app.event.entity.EventEntity;
import app.event.repository.EventRepository;
import event.app.dto.AuthResponse;
import event.app.dto.LoginRequest;
import event.app.dto.RegisterRequest;
import app.event.entity.NotificationPreferencesEntity;
import app.event.entity.Role;
import app.event.entity.UserEntity;
import app.event.exceptions.BadRequestException;
import app.event.exceptions.UnauthorizedException;
import app.event.repository.NotificationPreferencesRepository;
import app.event.repository.UserRepository;
import app.event.security.JwtService;
import app.event.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final NotificationPreferencesRepository notifRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.getEmail() == null || req.getPassword() == null || req.getPassword().length() < 8) {
            throw new BadRequestException("Неверные email или пароль");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }
        UserEntity user = userRepository.save(UserEntity.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build());
        notifRepository.save(NotificationPreferencesEntity.builder()
                .user(user).notifyNewEvents(true).notifyUpcoming(true).notifyBeforeHours(24).build());
        log.info("Registered user {}", user.getEmail());
        return new AuthResponse()
                .token(jwtService.generate(user.getId(), user.getEmail(), user.getRole()))
                .role(AuthResponse.RoleEnum.valueOf(user.getRole().name()));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        UserEntity u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Неверное имя пользователя или пароль"));
        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new UnauthorizedException("Неверное имя пользователя или пароль");
        }
        return new AuthResponse()
                .token(jwtService.generate(u.getId(), u.getEmail(), u.getRole()))
                .role(AuthResponse.RoleEnum.valueOf(u.getRole().name()));
    }

    @jakarta.annotation.PostConstruct
    public void setupDefaultAdmin() {
        String adminEmail = "admin@eventify.local";
        String rawPassword = "Admin12345!";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                admin -> {
                    admin.setPasswordHash(passwordEncoder.encode(rawPassword));
                    admin.setRole(Role.ADMIN);
                    userRepository.save(admin);
                    log.info("Пароль для {} успешно обновлен при старте", adminEmail);
                },
                () -> {
                    UserEntity admin = UserEntity.builder()
                            .email(adminEmail)
                            .passwordHash(passwordEncoder.encode(rawPassword))
                            .role(Role.ADMIN)
                            .build();
                    userRepository.save(admin);
                    log.info("Дефолтный админ {} создан с нуля", adminEmail);
                }
        );

        if (eventRepository.count() == 0) {
            eventRepository.save(EventEntity.builder()
                    .title("Добро пожаловать в Eventify!")
                    .description("Это демонстрационное мероприятие. Вы можете забронировать билет, чтобы протестировать уведомления в Telegram.")
                    .dateTime(OffsetDateTime.now().plusDays(7))
                    .totalTickets(50)
                    .coverUrl("https://unsplash.com")
                    .build());
            log.info("🚀 Тестовое мероприятие успешно создано");
        }
    }
}


