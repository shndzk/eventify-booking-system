package app.event.service.impl;


import app.event.entity.TelegramLinkEntity;
import app.event.exceptions.NotFoundException;
import app.event.repository.TelegramLinkRepository;
import app.event.repository.UserRepository;
import app.event.service.TelegramLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class TelegramLinkServiceImpl implements TelegramLinkService {
    private static final SecureRandom RND = new SecureRandom();
    private final TelegramLinkRepository linkRepo;
    private final UserRepository userRepo;

    @Override @Transactional
    public String createLinkCode(Long userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        var existing = linkRepo.findByUserIdAndLinkedFalse(userId);
        if (existing.isPresent()) return existing.get().getLinkCode();
        byte[] bytes = new byte[16];
        RND.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        linkRepo.save(TelegramLinkEntity.builder().user(user).linkCode(code).linked(false).build());
        return code;
    }
}

