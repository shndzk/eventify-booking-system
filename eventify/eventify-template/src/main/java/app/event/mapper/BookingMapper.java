package app.event.mapper;


import event.app.dto.BookingResponse;
import app.event.entity.BookingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final EventMapper eventMapper;

    public BookingResponse toResponse(BookingEntity b, int availableTickets) {
        return new BookingResponse(b.getId(), eventMapper.toResponse(b.getEvent(), availableTickets))
                .customerEmail(b.getUser().getEmail())
                .ticketCount(b.getTicketCount())
                .createdAt(b.getCreatedAt())
                .expiryTime(b.getExpiryTime())
                .confirmed(b.isConfirmed())
                .timezone(b.getTimezone());
    }
}

