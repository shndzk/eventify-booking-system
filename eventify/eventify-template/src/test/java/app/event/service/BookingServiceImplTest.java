package app.event.service;


import event.app.dto.CreateBookingRequest;
import app.event.entity.*;
import app.event.exceptions.BadRequestException;
import app.event.exceptions.ForbiddenException;
import app.event.mapper.BookingMapper;
import app.event.mapper.EventMapper;
import app.event.mapper.PageableMapper;
import app.event.repository.*;
import app.event.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock BookingRepository bookingRepo;
    @Mock EventRepository eventRepo;
    @Mock UserRepository userRepo;

    BookingServiceImpl service;
    UserEntity user;
    EventEntity event;

    @BeforeEach
    void setUp() {
        EventMapper em = new EventMapper();
        BookingMapper bm = new BookingMapper(em);
        PageableMapper pm = new PageableMapper();
        service = new BookingServiceImpl(bookingRepo, eventRepo, userRepo, bm, pm);
        ReflectionTestUtils.setField(service, "expiryHours", 24L);
        user = UserEntity.builder().id(1L).email("u@x.com").role(Role.USER).build();
        event = EventEntity.builder().id(10L).title("E").totalTickets(10)
                .dateTime(OffsetDateTime.now().plusDays(1)).build();
    }

    @Test
    void create_shouldSucceed() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepo.findById(10L)).thenReturn(Optional.of(event));
        when(bookingRepo.sumBookedTickets(10L)).thenReturn(3);
        when(bookingRepo.save(any(BookingEntity.class))).thenAnswer(inv -> {
            BookingEntity b = inv.getArgument(0); b.setId(100L); return b;
        });
        var resp = service.create(1L, new CreateBookingRequest(10L, 2));
        assertThat(resp.getId()).isEqualTo(100L);
        assertThat(resp.getTicketCount()).isEqualTo(2);
    }

    @Test
    void create_shouldFail_whenNotEnoughTickets() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepo.findById(10L)).thenReturn(Optional.of(event));
        when(bookingRepo.sumBookedTickets(10L)).thenReturn(9);
        assertThatThrownBy(() -> service.create(1L, new CreateBookingRequest(10L, 5)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void cancel_notOwner_throws() {
        BookingEntity b = BookingEntity.builder().id(77L).user(user).event(event).ticketCount(1).build();
        when(bookingRepo.findById(77L)).thenReturn(Optional.of(b));
        assertThatThrownBy(() -> service.cancel(999L, 77L, false))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void cancel_admin_ok() {
        BookingEntity b = BookingEntity.builder().id(77L).user(user).event(event).ticketCount(1).build();
        when(bookingRepo.findById(77L)).thenReturn(Optional.of(b));
        service.cancel(999L, 77L, true);
        verify(bookingRepo).delete(b);
    }
}

