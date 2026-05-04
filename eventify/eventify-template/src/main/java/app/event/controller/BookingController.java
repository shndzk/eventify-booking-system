package app.event.controller;


import event.app.api.BookingsApi;
import event.app.dto.BookingResponse;
import event.app.dto.CreateBookingRequest;
import event.app.dto.UpdateBookingRequest;
import app.event.security.CurrentUserService;
import app.event.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class BookingController implements BookingsApi {

    private final BookingService bookingService;
    private final CurrentUserService currentUser;

    @Override
    public ResponseEntity<List<BookingResponse>> bookingsGet() {
        return ResponseEntity.ok(bookingService.listOwn(currentUser.currentUserId()));
    }

    @Override
    public ResponseEntity<BookingResponse> bookingsPost(CreateBookingRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.create(currentUser.currentUserId(), req));
    }

    @Override
    public ResponseEntity<BookingResponse> bookingsIdGet(Long id) {
        return ResponseEntity.ok(bookingService.get(currentUser.currentUserId(), id, currentUser.isAdmin()));
    }

    @Override
    public ResponseEntity<BookingResponse> bookingsIdPut(Long id, UpdateBookingRequest req) {
        return ResponseEntity.ok(bookingService.update(currentUser.currentUserId(), id, req, currentUser.isAdmin()));
    }

    @Override
    public ResponseEntity<Void> bookingsIdDelete(Long id) {
        bookingService.cancel(currentUser.currentUserId(), id, currentUser.isAdmin());
        return ResponseEntity.noContent().build();
    }
}

