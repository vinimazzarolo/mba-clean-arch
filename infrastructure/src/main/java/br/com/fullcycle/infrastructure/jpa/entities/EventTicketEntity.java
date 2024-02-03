package br.com.fullcycle.infrastructure.jpa.entities;

import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventTicket;
import br.com.fullcycle.domain.event.EventTicketId;
import br.com.fullcycle.domain.event.ticket.TicketId;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "EventTicket")
@Table(name = "events_tickets")
public class EventTicketEntity {

    @Id
    private UUID eventTicketId;

    private UUID customerId;

    private UUID ticketId;

    private int ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    private EventEntity event;

    public EventTicketEntity() {

    }

    public EventTicketEntity(final UUID eventTicketId, final UUID customerId, final UUID ticketId, final int ordering, final EventEntity event) {
        this.eventTicketId = eventTicketId;
        this.customerId = customerId;
        this.ticketId = ticketId;
        this.ordering = ordering;
        this.event = event;
    }

    public static EventTicketEntity of(final EventEntity event, final EventTicket eventTicket) {
        return new EventTicketEntity(
                UUID.fromString(eventTicket.eventTicketId().value()),
                UUID.fromString(eventTicket.customerId().value()),
                eventTicket.ticketId() != null ? UUID.fromString(eventTicket.ticketId().value()) : null,
                eventTicket.ordering(),
                event
        );
    }

    public EventTicket toEventTicket() {
        return new EventTicket(
                EventTicketId.with(eventTicketId.toString()),
                EventId.with(this.event.id().toString()),
                CustomerId.with(this.customerId.toString()),
                this.ticketId != null ? TicketId.with(this.ticketId.toString()) : null,
                this.ordering

        );
    }

    public UUID eventTicketId() {
        return eventTicketId;
    }

    public void setEventTicketId(UUID eventTicketId) {
        this.eventTicketId = eventTicketId;
    }

    public UUID ticketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID customerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public int ordering() {
        return ordering;
    }

    public void setOrdering(int ordering) {
        this.ordering = ordering;
    }

    public EventEntity event() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventTicketEntity that = (EventTicketEntity) o;
        return Objects.equals(eventTicketId, that.eventTicketId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTicketId);
    }
}
