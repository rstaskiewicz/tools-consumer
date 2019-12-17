package com.gitlab.rstaskiewicz.tool.consumer.common.events.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
class EventsStorage {

    private final EventDescriptorRepository repository;

    void save(EventDescriptor event) {
        repository.save(event);
    }

    List<EventDescriptor> fetch() {
       return repository.findTop100OrderByOccurredAt();
    }

    void published(List<EventDescriptor> events) {
        repository.deleteAll(events);
    }
}

interface EventDescriptorRepository extends CrudRepository<EventDescriptor, Long> {

    @Query("SELECT * FROM event_descriptor ORDER BY occurred_at FETCH FIRST 100 ROWS ONLY")
    List<EventDescriptor> findTop100OrderByOccurredAt();
}
