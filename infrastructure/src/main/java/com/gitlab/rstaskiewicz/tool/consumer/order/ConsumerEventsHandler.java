//package com.gitlab.rstaskiewicz.tool.consumer.order;
//
//import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.Version;
//import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
//import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlaced;
//import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerId;
//import com.gitlab.rstaskiewicz.tool.consumer.salesbranch.SalesBranchId;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.stream.annotation.StreamListener;
//import org.springframework.messaging.handler.annotation.Payload;
//
//import static com.gitlab.rstaskiewicz.tool.consumer.common.events.ConsumerEventSink.INPUT;
//import static io.vavr.API.$;
//import static io.vavr.API.Case;
//import static io.vavr.API.Match;
//import static io.vavr.Predicates.instanceOf;
//
//@Slf4j
//@RequiredArgsConstructor
//public class ConsumerEventsHandler {
//
//    private final Orders orders;
//
//    @StreamListener(target = INPUT, condition = "headers['type'] == 'order-placed'")
//    void handle(@Payload OrderPlaced event) {
//        log.debug("Received OrderPlaced event for consumer: {} and order: {}", event.getConsumerId(), event.getOrderId());
//        orders.save(new PlacedOrder(
//                new OrderId(event.getOrderId()),
//                new ConsumerId(event.getConsumerId()),
//                new SalesBranchId(event.getSalesBranchId()),
//                event.getOrderedAt(),
//                Version.zero()));
//    }
//
//    @StreamListener(target = INPUT, condition = "headers['type'] == 'order-cancelled'")
//    void handle(@Payload OrderCanceled event) {
//        log.debug("Received OrderCanceled event for consumer: {} and order: {}", event.getConsumerId(), event.getOrderId());
//        orders.findBy(new OrderId(event.getOrderId()))
//                .map(order -> handleOrderCancelled(order, event))
//                .map(this::saveOrder);
//    }
//
//    private Order handleOrderCancelled(Order order, OrderCanceled event) {
//        return Match(order).of(
//                Case($(instanceOf(PlacedOrder.class)), placedOrder -> placedOrder.handle(event)),
//                Case($(), () -> order));
//    }
//
//    private Order saveOrder(Order order) {
//        orders.save(order);
//        return order;
//    }
//}
