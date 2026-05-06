package app.event.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationPreferencesEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "notify_new_events", nullable = false)
    private boolean notifyNewEvents;

    @Column(name = "notify_upcoming", nullable = false)
    private boolean notifyUpcoming;

    @Column(name = "notify_before_hours", nullable = false)
    private int notifyBeforeHours;
}

