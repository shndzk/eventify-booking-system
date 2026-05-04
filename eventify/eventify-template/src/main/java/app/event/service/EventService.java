package app.event.service;


import event.app.dto.EventCreateRequest;
import event.app.dto.EventResponse;
import event.app.dto.EventUpdateRequest;
import event.app.dto.PageableEventResponse;
import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;

public interface EventService {
    PageableEventResponse list(OffsetDateTime from, OffsetDateTime to, Pageable pageable);
    EventResponse getById(Long id);
    EventResponse create(EventCreateRequest req);
    EventResponse update(Long id, EventUpdateRequest req);
    void delete(Long id);
}

