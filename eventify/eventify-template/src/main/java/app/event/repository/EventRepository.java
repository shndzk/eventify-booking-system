package app.event.repository;


import app.event.entity.EventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("""
       select e from EventEntity e
       where (coalesce(:from, null) is null or e.dateTime >= :from)
         and (coalesce(:to, null) is null or e.dateTime <= :to)
       """)
    Page<EventEntity> search(OffsetDateTime from, OffsetDateTime to, Pageable pageable);

}

