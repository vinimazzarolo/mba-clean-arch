package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.event.ticket.Ticket;
import br.com.fullcycle.domain.event.ticket.TicketId;
import br.com.fullcycle.domain.event.ticket.TicketRepository;
import br.com.fullcycle.infrastructure.jpa.entities.OutboxEntity;
import br.com.fullcycle.infrastructure.jpa.entities.TicketEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.OutboxJpaRepository;
import br.com.fullcycle.infrastructure.jpa.repositories.TicketJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class TicketDatabaseRepository implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper mapper;

    public TicketDatabaseRepository(final TicketJpaRepository ticketJpaRepository, final OutboxJpaRepository outboxJpaRepository, final ObjectMapper mapper) {
        this.ticketJpaRepository = Objects.requireNonNull(ticketJpaRepository);
        this.outboxJpaRepository = Objects.requireNonNull(outboxJpaRepository);
        this.mapper = mapper;
    }

    @Override
    public Optional<Ticket> ticketOfId(final TicketId ticketId) {
        Objects.requireNonNull(ticketId, "ticketId cannot be null");
        return this.ticketJpaRepository.findById(UUID.fromString(ticketId.value()))
                .map(TicketEntity::toTicket);
    }


    @Override
    @Transactional
    public Ticket create(Ticket ticket) {
        return save(ticket);
    }

    @Override
    @Transactional
    public Ticket update(Ticket ticket) {
        return save(ticket);
    }

    private Ticket save(Ticket ticket) {
        this.outboxJpaRepository.saveAll(
                ticket.allDomainEvents()
                    .stream()
                    .map(it -> OutboxEntity.of(it, this::toJson))
                    .toList()
                );
        return this.ticketJpaRepository.save(TicketEntity.of(ticket)).toTicket();
    }

    @Override
    public void deleteAll() {
        this.ticketJpaRepository.deleteAll();
    }

    private String toJson(DomainEvent domainEvent) {
        try {
            return this.mapper.writeValueAsString(domainEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
