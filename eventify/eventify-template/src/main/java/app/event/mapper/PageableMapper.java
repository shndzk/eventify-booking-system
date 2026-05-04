package app.event.mapper;

import event.app.dto.EventResponse;
import event.app.dto.Pageable;
import event.app.dto.PageableEventResponse;
import event.app.dto.PageableObject;
import event.app.dto.SortObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageableMapper {

    public org.springframework.data.domain.Pageable toSpring(Pageable p) {
        int page = (p == null || p.getPage() == null) ? 0 : p.getPage();
        int size = (p == null || p.getSize() == null) ? 20 : p.getSize();

        List<Sort.Order> orders = new ArrayList<>();
        if (p != null && p.getSort() != null) {
            for (String s : p.getSort()) {
                if (s == null || s.isBlank()) continue;
                if (s.equalsIgnoreCase("asc") || s.equalsIgnoreCase("desc")) {
                    continue;
                }
                String[] parts = s.split(",");
                String property = parts[0].trim();
                Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                        ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders.add(new Sort.Order(dir, property));
            }
        }
        return orders.isEmpty() ? PageRequest.of(page, size) : PageRequest.of(page, size, Sort.by(orders));
    }

    public PageableEventResponse toPageableResponse(Page<EventResponse> page) {
        if (page == null) return null;

        return new PageableEventResponse()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .pageable(mapPageableObject(page.getPageable()))
                .sort(mapSortObject(page.getSort()));
    }

    private PageableObject mapPageableObject(org.springframework.data.domain.Pageable p) {
        return new PageableObject()
                .offset(p.getOffset())
                .pageNumber(p.getPageNumber())
                .pageSize(p.getPageSize())
                .paged(p.isPaged())
                .unpaged(p.isUnpaged())
                .sort(mapSortObject(p.getSort()));
    }

    private SortObject mapSortObject(Sort sort) {
        return new SortObject()
                .empty(sort.isEmpty())
                .sorted(sort.isSorted())
                .unsorted(sort.isUnsorted());
    }
}
