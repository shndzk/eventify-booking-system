package app.event.service;


import event.app.dto.BookingResponse;
import event.app.dto.CreateBookingRequest;
import event.app.dto.PageableBookingResponse;
import event.app.dto.UpdateBookingRequest;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingResponse create(Long userId, CreateBookingRequest req);
    List<BookingResponse> listOwn(Long userId);
    BookingResponse get(Long userId, Long bookingId, boolean isAdmin);
    BookingResponse update(Long userId, Long bookingId, UpdateBookingRequest req, boolean isAdmin);
    void cancel(Long userId, Long bookingId, boolean isAdmin);
    PageableBookingResponse adminList(Long eventId, boolean unconfirmedOnly, Pageable pageable);
    void adminDelete(Long bookingId);
    void adminConfirm(Long bookingId);
}

