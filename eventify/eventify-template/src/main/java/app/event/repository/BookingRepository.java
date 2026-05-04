package app.event.repository;


import app.event.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findAllByUserId(Long userId);

    @Query("select coalesce(sum(b.ticketCount),0) from BookingEntity b where b.event.id = :eventId")
    int sumBookedTickets(@Param("eventId") Long eventId);

    @Query("""
           select b from BookingEntity b
           where (:eventId is null or b.event.id = :eventId)
             and (:unconfirmedOnly = false or b.confirmed = false)
           """)
    Page<BookingEntity> adminSearch(@Param("eventId") Long eventId,
                                    @Param("unconfirmedOnly") boolean unconfirmedOnly,
                                    Pageable pageable);
}

