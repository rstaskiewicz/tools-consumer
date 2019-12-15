package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.common.event.DomainEvents;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.ConsumerCreated;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static java.time.ZoneId.systemDefault;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Repository
@RequiredArgsConstructor
class ConsumersDatabaseRepository implements Consumers {

    private final ConsumerEntityRepository consumerEntityRepository;
    private final DomainModelMapper domainModelMapper;
    private final DomainEvents events;

    @Override
    public Option<Consumer> findBy(ConsumerId consumerId) {
        return Option.of(consumerEntityRepository.findByConsumerId(consumerId.getId()))
                .map(domainModelMapper::map);
    }

    @Override
    public Consumer publish(ConsumerEvent event) {
        Consumer result = Match(event).of(
                Case($(instanceOf(ConsumerCreated.class)), this::createNewConsumer),
                Case($(), this::handleNextEvent));
        events.publish(event);
        return result;
    }

    private Consumer createNewConsumer(ConsumerCreated event) {
        ConsumerEntity entity = consumerEntityRepository.save(new ConsumerEntity(event.consumerId()));
        return domainModelMapper.map(entity);
    }

    private Consumer handleNextEvent(ConsumerEvent event) {
        ConsumerEntity entity = consumerEntityRepository.findByConsumerId(event.getConsumerId());
        entity = entity.handle(event);
        entity = consumerEntityRepository.save(entity);
        return domainModelMapper.map(entity);
    }
}

interface ConsumerEntityRepository extends CrudRepository<ConsumerEntity, Long> {

    @Query("SELECT * FROM consumer_entity where consumer_id = :consumerId")
    ConsumerEntity findByConsumerId(@Param("consumerId") UUID consumerId);
}

@Component
class DomainModelMapper {

    private final ConsumerFactory consumerFactory = new ConsumerFactory();

    Consumer map(ConsumerEntity entity) {
        return consumerFactory.create(
                new ConsumerId(entity.consumerId),
                mapConsumerOrders(entity),
                mapOverduePayments(entity));
    }

    Map<LocalDate, Set<ConsumerOrder>> mapConsumerOrders(ConsumerEntity consumerEntity) {
        return consumerEntity.consumerOrders.stream()
                .collect(groupingBy(ConsumerOrderEntity::getOrderedAt, toSet()))
                .entrySet().stream()
                .collect(toMap(
                        (Map.Entry<Instant, Set<ConsumerOrderEntity>> entry) -> entry.getKey().atZone(systemDefault()).toLocalDate(), entry ->
                        entry.getValue().stream()
                                .map(entity -> new ConsumerOrder(new OrderId(entity.orderId), new SalesBranchId(entity.salesBranchId)))
                                .collect(toSet())));
    }

    Map<SalesBranchId, Set<OrderId>> mapOverduePayments(ConsumerEntity consumerEntity) {
        return consumerEntity.overduePayments.stream()
                .collect(groupingBy(OverduePaymentEntity::getSalesBranchId, toSet()))
                .entrySet().stream()
                .collect(toMap(
                        (Map.Entry<UUID, Set<OverduePaymentEntity>> entry) -> new SalesBranchId(entry.getKey()), entry ->
                                entry.getValue().stream()
                                        .map(entity -> new OrderId(entity.orderId))
                                        .collect(toSet())));
    }
}
