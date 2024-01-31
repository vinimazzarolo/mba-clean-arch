package br.com.fullcycle.application.event;

import br.com.fullcycle.application.UseCase;
import br.com.fullcycle.domain.customer.CustomerId;
import br.com.fullcycle.domain.customer.CustomerRepository;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import br.com.fullcycle.domain.exceptions.ValidationException;

import java.time.Instant;
import java.util.Objects;

public class SubscribeCustomerToEventUseCase extends UseCase<SubscribeCustomerToEventUseCase.Input, SubscribeCustomerToEventUseCase.Output> {

    private final CustomerRepository customerRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public SubscribeCustomerToEventUseCase(final EventRepository eventRepository, final CustomerRepository customerRepository, final TicketRepository ticketRepository) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.ticketRepository = Objects.requireNonNull(ticketRepository);
    }

    @Override
    public Output execute(final Input input) {
        var customer = customerRepository.customerOfId(CustomerId.with(input.customerId()))
                .orElseThrow(() -> new ValidationException("Customer not found"));

        var event = eventRepository.eventOfId(EventId.with(input.eventId()))
                .orElseThrow(() -> new ValidationException("Event not found"));


        final Ticket ticket = event.reserveTicket(customer.customerId());

        ticketRepository.create(ticket);
        eventRepository.update(event);

        return new Output(event.eventId().value(), ticket.ticketId().value(), ticket.status().name(), ticket.reserverdAt());
    }

    public record Input(String customerId, String eventId) {}

    public record Output(String eventId, String ticketId, String ticketStatus, Instant reservationDate) {}
}
