package app.event.mapper;

import event.app.dto.EventCreateRequest;
import event.app.dto.EventResponse;
import event.app.dto.BookingResponse;
import app.event.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponse toResponse(EventEntity e, int availableTickets) {
        if (e == null) return null;

        return new EventResponse()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .dateTime(e.getDateTime())
                .totalTickets(e.getTotalTickets())
                .availableTickets(availableTickets)
                .coverUrl(e.getCoverUrl());
    }

    public BookingResponse toBookingEvent(EventEntity e, int availableTickets) {
        if (e == null) return null;

        return new BookingResponse()
                .id(null)
                .event(toResponse(e, availableTickets));
    }

    public EventEntity toEntity(EventCreateRequest r) {
        if (r == null) return null;

        return EventEntity.builder()
                .title(r.getTitle())
                .description(r.getDescription())
                .dateTime(r.getDateTime())
                .totalTickets(r.getTotalTickets())
                .coverUrl(r.getCoverUrl())
                .build();
    }
}
