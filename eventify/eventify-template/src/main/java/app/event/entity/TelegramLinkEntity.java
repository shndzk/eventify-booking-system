package app.event.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "telegram_links")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TelegramLinkEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "link_code", nullable = false, unique = true, length = 64)
    private String linkCode;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(nullable = false)
    private boolean linked;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}

