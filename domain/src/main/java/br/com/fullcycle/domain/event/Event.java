package br.com.fullcycle.domain.event;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.exceptions.ValidationException;
import br.com.fullcycle.domain.partner.Partner;
import br.com.fullcycle.domain.partner.PartnerId;
import br.com.fullcycle.domain.person.Name;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Event {

    private static final int ONE = 1;
    private final EventId eventId;
    private final Set<EventTicket> tickets;
    private final Set<DomainEvent> domainEvents;
    private Name name;
    private LocalDate date;
    private int totalSpots;
    private PartnerId partnerId;

    public Event(final EventId eventId, final String name, final String date, final Integer totalSpots, final PartnerId partnerId, final Set<EventTicket> tickets) {
        this(eventId, tickets);
        this.setName(name);
        this.setDate(date);
        this.setTotalSpots(totalSpots);
        this.setPartnerId(partnerId);
    }

    private Event(final EventId eventId, final Set<EventTicket> tickets) {
        if (eventId == null) {
            throw new ValidationException("Invalid value for EventId");
        }
        this.eventId = eventId;
        this.tickets = tickets != null ? tickets : new HashSet<>(0);
        this.domainEvents = new HashSet<>(2);
    }

    public static Event newEvent(final String name, final String date, final Integer totalSpots, final Partner partner) {
        return new Event(EventId.unique(), name, date, totalSpots, partner.partnerId(), null);
    }

    public static Event restore(final String id, final String name, final String date, final int totalSpots, final String partnerId, final Set<EventTicket> tickets) {
        return new Event(EventId.with(id), name, date, totalSpots, PartnerId.with(partnerId), tickets);
    }

    public EventTicket reserveTicket(final CustomerId customerId) {
        this.allTickets().stream()
                .filter(it -> Objects.equals(it.customerId(), customerId))
                .findFirst()
                .ifPresent(it -> {
                    throw new ValidationException("Email already registered");
                });

        if (totalSpots() < this.tickets.size() + ONE) {
            throw new ValidationException("Event sold out");
        }

        final var aTicket = EventTicket.newTicket(eventId(), customerId, this.tickets.size() + ONE);

        this.tickets.add(aTicket);

        this.domainEvents.add(new EventTicketReserved(aTicket.eventTicketId(), eventId, customerId));

        return aTicket;
    }

    public EventId eventId() {
        return eventId;
    }

    public Name name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public int totalSpots() {
        return totalSpots;
    }

    public PartnerId partnerId() {
        return partnerId;
    }

    public Set<EventTicket> allTickets() {
        return Collections.unmodifiableSet(tickets);
    }

    public Set<DomainEvent> allDomainEvents() {
        return Collections.unmodifiableSet(domainEvents);
    }

    private void setName(final String name) {
        this.name = new Name(name);
    }

    private void setDate(final String date) {
        if (date == null) {
            throw new ValidationException("Invalid value for date");
        }

        try {
            this.date = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (RuntimeException e) {
            throw new ValidationException("Invalid value for date", e);
        }

    }

    private void setTotalSpots(final Integer totalSpots) {
        if (totalSpots == null || totalSpots <= 0) {
            throw new ValidationException("Invalid value for totalSpots");
        }
        this.totalSpots = totalSpots;
    }

    private void setPartnerId(final PartnerId partnerId) {
        if (partnerId == null) {
            throw new ValidationException("Invalid value for partnerId");
        }
        this.partnerId = partnerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
