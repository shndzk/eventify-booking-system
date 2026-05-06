package app.event.controller;



import event.app.api.AdminApi;
import event.app.dto.*;
import app.event.mapper.PageableMapper;
import app.event.service.BookingService;
import app.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController implements AdminApi {

    private final EventService eventService;
    private final BookingService bookingService;
    private final PageableMapper pageableMapper;

    @Override
    public ResponseEntity<EventResponse> eventsPost(EventCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(req));
    }

    @Override
    public ResponseEntity<EventResponse> eventsIdPut(Long id, EventUpdateRequest req) {
        return ResponseEntity.ok(eventService.update(id, req));
    }

    @Override
    public ResponseEntity<Void> eventsIdDelete(Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<PageableBookingResponse> adminBookingsGet(Pageable pageable, Long eventId, Boolean unconfirmedOnly) {
        boolean unconfirmed = unconfirmedOnly != null && unconfirmedOnly;
        return ResponseEntity.ok(bookingService.adminList(eventId, unconfirmed, pageableMapper.toSpring(pageable)));
    }

    @Override
    public ResponseEntity<Void> adminBookingsIdConfirm(Long id) {
        bookingService.adminConfirm(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> adminBookingsIdDelete(Long id) {
        bookingService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }
}

