package com.gitlab.rstaskiewicz.tool.consumer.order;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootIsStale;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.FindPlacedOrder;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Cancelled;
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Paid;
import static com.gitlab.rstaskiewicz.tool.consumer.order.OrderEntity.OrderState.Placed;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.sql.Timestamp.from;

@Repository
@RequiredArgsConstructor
class OrderDatabaseRepository implements Orders, FindPlacedOrder {

    private final JdbcTemplate orders;

    @Override
    public Option<Order> findBy(OrderId orderId) {
        return findOrderById(orderId)
                .map(OrderEntity::toDomainModel);
    }

    @Override
    public void save(Order order) {
        findBy(order.getOrderId())
                .map(entity -> updateOptimistically(order))
                .onEmpty(() -> insertNew(order));
    }

    @Override
    public Option<PlacedOrder> findPlacedOrder(OrderId orderId) {
        return Match(findBy(orderId)).of(
                Case($Some($(instanceOf(PlacedOrder.class))), Option::of),
                Case($(), Option::none));
    }

    private Option<OrderEntity> findOrderById(OrderId orderId) {
        return Try.ofSupplier(() -> of(orders.queryForObject("SELECT * FROM order_entity WHERE order_id = ?",
                new BeanPropertyRowMapper<>(OrderEntity.class), orderId.getId())))
                .getOrElse(none());
    }

    private int updateOptimistically(Order order) {
        int result = Match(order).of(
                Case($(instanceOf(PlacedOrder.class)), this::update),
                Case($(instanceOf(PaidOrder.class)), this::update),
                Case($(instanceOf(CancelledOrder.class)), this::update)
        );
        if (result == 0) {
            throw new AggregateRootIsStale("The order has been updated in the meantime: " + order.getOrderId().getId());
        }
        return result;
    }

    private int update(PlacedOrder order) {
        return orders.update("UPDATE order_entity" +
                        " SET order_state = ?, version = ?" +
                        " WHERE order_id = ? AND version = ?",
                Placed.toString(),
                order.getVersion().getVersion() + 1,
                order.getOrderId().getId(),
                order.getVersion().getVersion());
    }

    private int update(PaidOrder order) {
        return orders.update("UPDATE order_entity" +
                        " SET order_state = ?, paid_when = ?, version = ?" +
                        " WHERE order_id = ? AND version = ?",
                Paid.toString(),
                from(order.getPaidWhen()),
                order.getVersion().getVersion() + 1,
                order.getOrderId().getId(),
                order.getVersion().getVersion());
    }

    private int update(CancelledOrder order) {
        return orders.update("UPDATE order_entity" +
                        " SET order_state = ?, cancelled_why = ?, version = ?" +
                        " WHERE order_id = ? AND version = ?",
                Cancelled.toString(),
                order.getCancelledWhy().toString(),
                order.getVersion().getVersion() + 1,
                order.getOrderId().getId(),
                order.getVersion().getVersion());
    }

    private void insertNew(Order order) {
        Match(order).of(Case($(instanceOf(PlacedOrder.class)), this::insert));
    }

    private int insert(PlacedOrder order) {
        return orders.update("INSERT INTO order_entity" +
                        " (order_id, order_state, placed_by_consumer, placed_at_branch, payment_till, version)" +
                        " VALUES (?, ?, ? , ?, ?, ?)",
                order.getOrderId().getId(),
                Placed.toString(),
                order.getByConsumer().getId(),
                order.getPlacedAt().getId(),
                from(order.getPaymentTill()),
                0);
    }
}
