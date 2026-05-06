package app.event.service.impl;


import event.app.dto.BookingResponse;
import event.app.dto.CreateBookingRequest;
import event.app.dto.PageableBookingResponse;
import event.app.dto.UpdateBookingRequest;
import event.app.dto.PageableObject;
import event.app.dto.SortObject;

import app.event.entity.BookingEntity;
import app.event.entity.EventEntity;
import app.event.entity.UserEntity;
import app.event.exceptions.BadRequestException;
import app.event.exceptions.ForbiddenException;
import app.event.exceptions.NotFoundException;
import app.event.mapper.BookingMapper;
import app.event.mapper.PageableMapper;
import app.event.repository.BookingRepository;
import app.event.repository.EventRepository;
import app.event.repository.UserRepository;
import app.event.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final EventRepository eventRepo;
    private final UserRepository userRepo;
    private final BookingMapper bookingMapper;
    private final PageableMapper pageableMapper;

    @Value("${app.booking.expiry-hours:24}")
    private long expiryHours;

    @Override
    @Transactional
    public BookingResponse create(Long userId, CreateBookingRequest r) {
        UserEntity user = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        EventEntity ev = eventRepo.findById(r.getEventId()).orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
        if (ev.getDateTime().isBefore(OffsetDateTime.now()))
            throw new BadRequestException("Мероприятие уже прошло");
        int booked = bookingRepo.sumBookedTickets(ev.getId());
        if (booked + r.getTicketCount() > ev.getTotalTickets())
            throw new BadRequestException("Недостаточно свободных мест");
        BookingEntity b = bookingRepo.save(BookingEntity.builder()
                .user(user).event(ev).ticketCount(r.getTicketCount())
                .expiryTime(OffsetDateTime.now().plusHours(expiryHours))
                .confirmed(false).timezone(ZoneOffset.UTC.getId()).build());
        log.info("User {} created booking {}", userId, b.getId());
        return bookingMapper.toResponse(b, ev.getTotalTickets() - booked - r.getTicketCount());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> listOwn(Long userId) {
        return bookingRepo.findAllByUserId(userId).stream()
                .map(b -> bookingMapper.toResponse(b, avail(b.getEvent()))).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse get(Long userId, Long id, boolean isAdmin) {
        BookingEntity b = load(id);
        ensureOwner(userId, b, isAdmin);
        return bookingMapper.toResponse(b, avail(b.getEvent()));
    }

    @Override
    @Transactional
    public BookingResponse update(Long userId, Long id, UpdateBookingRequest r, boolean isAdmin) {
        BookingEntity b = load(id);
        ensureOwner(userId, b, isAdmin);
        if (r != null && r.getTicketCount() != null && !r.getTicketCount().equals(b.getTicketCount())) {
            int others = bookingRepo.sumBookedTickets(b.getEvent().getId()) - b.getTicketCount();
            if (others + r.getTicketCount() > b.getEvent().getTotalTickets())
                throw new BadRequestException("Недостаточно свободных мест");
            b.setTicketCount(r.getTicketCount());
        }
        return bookingMapper.toResponse(b, avail(b.getEvent()));
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long id, boolean isAdmin) {
        BookingEntity b = load(id);
        ensureOwner(userId, b, isAdmin);
        bookingRepo.delete(b);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableBookingResponse adminList(Long eventId, boolean unconfirmedOnly, Pageable pageable) {
        Page<BookingEntity> page = bookingRepo.adminSearch(eventId, unconfirmedOnly, pageable);

        SortObject sortInfo = new SortObject()
                .sorted(page.getSort().isSorted())
                .unsorted(page.getSort().isUnsorted())
                .empty(page.getSort().isEmpty());

        PageableObject pageInfo = new PageableObject()
                .offset(page.getPageable().getOffset())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .paged(page.getPageable().isPaged())
                .unpaged(page.getPageable().isUnpaged());

        PageableBookingResponse resp = new PageableBookingResponse()
                .pageable(pageInfo)
                .sort(sortInfo)
                .last(page.isLast())
                .first(page.isFirst())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty());

        page.getContent().forEach(b -> resp.addContentItem(bookingMapper.toResponse(b, avail(b.getEvent()))));
        return resp;
    }


    @Override
    @Transactional
    public void adminDelete(Long id) {
        if (!bookingRepo.existsById(id)) throw new NotFoundException("Бронирование не найдено");
        bookingRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void adminConfirm(Long id) {
        BookingEntity b = load(id);
        b.setConfirmed(true);
    }

    private BookingEntity load(Long id) {
        return bookingRepo.findById(id).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
    private void ensureOwner(Long userId, BookingEntity b, boolean isAdmin) {
        if (!isAdmin && !b.getUser().getId().equals(userId))
            throw new ForbiddenException("Доступ запрещён");
    }
    private int avail(EventEntity e) {
        return e.getTotalTickets() - bookingRepo.sumBookedTickets(e.getId());
    }
}

