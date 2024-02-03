package br.com.fullcycle.infrastructure.repositories;

import br.com.fullcycle.domain.DomainEvent;
import br.com.fullcycle.domain.event.Event;
import br.com.fullcycle.domain.event.EventId;
import br.com.fullcycle.domain.event.EventRepository;
import br.com.fullcycle.infrastructure.jpa.entities.EventEntity;
import br.com.fullcycle.infrastructure.jpa.entities.OutboxEntity;
import br.com.fullcycle.infrastructure.jpa.repositories.EventJpaRepository;
import br.com.fullcycle.infrastructure.jpa.repositories.OutboxJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventDatabaseRepository implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final OutboxJpaRepository outboxJpaRepository;
    private final ObjectMapper objectMapper;

    public EventDatabaseRepository(EventJpaRepository eventJpaRepository, final OutboxJpaRepository outboxJpaRepository, ObjectMapper mapper) {
        this.eventJpaRepository = Objects.requireNonNull(eventJpaRepository);
        this.outboxJpaRepository = Objects.requireNonNull(outboxJpaRepository);
        this.objectMapper = mapper;
    }

    @Override
    public Optional<Event> eventOfId(final EventId eventId) {
        Objects.requireNonNull(eventId, "eventId cannot be null");
        return this.eventJpaRepository.findById(UUID.fromString(eventId.value()))
                .map(EventEntity::toEvent);
    }


    @Override
    @Transactional
    public Event create(Event event) {
        save(event);
        return this.eventJpaRepository.save(EventEntity.of(event)).toEvent();
    }


    @Override
    @Transactional
    public Event update(Event event) {
        save(event);
        return this.eventJpaRepository.save(EventEntity.of(event)).toEvent();
    }

    @Override
    public void deleteAll() {
        this.eventJpaRepository.deleteAll();
    }

    private void save(Event event) {
        this.outboxJpaRepository.saveAll(
                event.allDomainEvents()
                    .stream()
                    .map(it -> OutboxEntity.of(it, this::toJson))
                    .toList()
        );
    }

    private String toJson(DomainEvent domainEvent) {
        try {
            return this.objectMapper.writeValueAsString(domainEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
