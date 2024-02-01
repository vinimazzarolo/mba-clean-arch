package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import br.com.fullcycle.infrastructure.jpa.entities.TicketEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.TicketJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class TicketDatabaseRepository implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;

    public TicketDatabaseRepository(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = Objects.requireNonNull(ticketJpaRepository);
    }

    @Override
    public Optional<Ticket> ticketOfId(final TicketId ticketId) {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        return this.ticketJpaRepository.findById(UUID.fromString(ticketId.value()))
                .map(TicketEntity::toTicket);
    }


    @Override
    @Transactional
    public Ticket create(Ticket customer) {
        return this.ticketJpaRepository.save(TicketEntity.of(customer)).toTicket();
    }

    @Override
    @Transactional
    public Ticket update(Ticket customer) {
        return this.ticketJpaRepository.save(TicketEntity.of(customer)).toTicket();
    }

    @Override
    public void deleteAll() {
        this.ticketJpaRepository.deleteAll();
    }
}
