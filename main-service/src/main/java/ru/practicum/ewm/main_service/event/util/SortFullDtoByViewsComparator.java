package ru.practicum.ewm.main_service.event.util;


import ru.practicum.ewm.main_service.event.dto.EventFullDto;

import java.util.Comparator;

public class SortFullDtoByViewsComparator implements Comparator<EventFullDto> {

    @Override
    public int compare(EventFullDto e1, EventFullDto e2) {
        return (int) (e2.getViews() - e1.getViews());
    }

    @Override
    public Comparator<EventFullDto> reversed() {
        return Comparator.super.reversed();
    }
}
