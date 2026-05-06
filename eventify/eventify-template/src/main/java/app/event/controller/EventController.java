package app.event.controller;


import event.app.api.EventsApi;
import event.app.dto.EventResponse;
import event.app.dto.Pageable;
import event.app.dto.PageableEventResponse;
import app.event.mapper.PageableMapper;
import app.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@org.springframework.web.bind.annotation.CrossOrigin(origins = "http://localhost:3000")
public class EventController implements EventsApi {

    private final EventService eventService;
    private final PageableMapper pageableMapper;

    @Override
    public ResponseEntity<PageableEventResponse> eventsGet(Pageable pageable, OffsetDateTime from, OffsetDateTime to) {
        return ResponseEntity.ok(eventService.list(from, to, pageableMapper.toSpring(pageable)));
    }

    @Override
    public ResponseEntity<EventResponse> eventsIdGet(Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }
}

