package br.com.fullcycle.domain.event;

import java.util.Optional;

public interface EventRepository {

    Optional<Event> eventOfId(EventId eventId);

    Event create(Event customer);

    Event update(Event customer);

    void deleteAll();
}
