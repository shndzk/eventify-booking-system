package app.event.service.impl;


import event.app.dto.*;
import app.event.entity.EventEntity;
import app.event.exceptions.BadRequestException;
import app.event.exceptions.NotFoundException;
import app.event.mapper.EventMapper;
import app.event.mapper.PageableMapper;
import app.event.repository.BookingRepository;
import app.event.repository.EventRepository;
import app.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper eventMapper;
    private final PageableMapper pageableMapper;

    @Override
    @Transactional(readOnly = true)
    public PageableEventResponse list(OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        Page<EventEntity> page = eventRepository.search(from, to, pageable);

        PageableObject pageInfo = new PageableObject()
                .offset(page.getPageable().getOffset())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .paged(page.getPageable().isPaged())
                .unpaged(page.getPageable().isUnpaged());

        SortObject sortInfo = new SortObject()
                .sorted(page.getSort().isSorted())
                .unsorted(page.getSort().isUnsorted())
                .empty(page.getSort().isEmpty());

        PageableEventResponse resp = new PageableEventResponse()
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

        page.getContent().forEach(e -> resp.addContentItem(eventMapper.toResponse(e, available(e))));
        return resp;
    }


    @Override
    @Transactional(readOnly = true)
    public EventResponse getById(Long id) {
        EventEntity e = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Мероприятие не найдено"));
        return eventMapper.toResponse(e, available(e));
    }

    @Override
    @Transactional
    public EventResponse create(EventCreateRequest r) {
        if (r.getDateTime().isBefore(OffsetDateTime.now()))
            throw new BadRequestException("Дата мероприятия не может быть в прошлом");
        EventEntity saved = eventRepository.save(eventMapper.toEntity(r));
        log.info("Event created id={}", saved.getId());
        return eventMapper.toResponse(saved, saved.getTotalTickets());
    }

    @Override
    @Transactional
    public EventResponse update(Long id, EventUpdateRequest r) {
        EventEntity e = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Ресурс не найден"));
        if (r.getTitle() != null)       e.setTitle(r.getTitle());
        if (r.getDescription() != null) e.setDescription(r.getDescription());
        if (r.getDateTime() != null)    e.setDateTime(r.getDateTime());
        if (r.getCoverUrl() != null)    e.setCoverUrl(r.getCoverUrl());
        if (r.getTotalTickets() != null) {
            int booked = bookingRepository.sumBookedTickets(id);
            if (r.getTotalTickets() < booked)
                throw new BadRequestException("Нельзя уменьшить число мест ниже забронированных: " + booked);
            e.setTotalTickets(r.getTotalTickets());
        }
        return eventMapper.toResponse(e, available(e));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) throw new NotFoundException("Ресурс не найден");
        eventRepository.deleteById(id);
    }

    private int available(EventEntity e) {
        return e.getTotalTickets() - bookingRepository.sumBookedTickets(e.getId());
    }
}

