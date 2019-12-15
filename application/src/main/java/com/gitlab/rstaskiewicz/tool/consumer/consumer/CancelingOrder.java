package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootNotFoundException;
import com.gitlab.rstaskiewicz.tool.consumer.common.commands.Result;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCanceled;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderCancelingFailed;
import com.gitlab.rstaskiewicz.tool.consumer.order.OrderId;
import com.gitlab.rstaskiewicz.tool.consumer.order.PlacedOrder;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.gitlab.rstaskiewicz.tool.consumer.common.commands.Result.Success;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelingOrder {

    private final FindPlacedOrder findPlacedOrder;
    private final Consumers consumers;

    public Try<Result> cancel(@NonNull CancelOrderCommand command) {
        return Try.of(() -> {
            PlacedOrder placedOrder = find(command.getOrderId());
            Consumer consumer = find(command.getConsumerId());
            Either<OrderCancelingFailed, OrderCanceled> result = consumer.cancelOrder(placedOrder, command.getReason());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents)
            );
        }).onFailure(ex -> log.error("Failed to cancel an order", ex));
    }

    private Result publishEvents(OrderCanceled event) {
        consumers.publish(event);
        return Success;
    }

    private Result publishEvents(OrderCancelingFailed event) {
        consumers.publish(event);
        return Result.Rejection;
    }

    private PlacedOrder find(OrderId orderId) {
        return findPlacedOrder.findPlacedOrder(orderId)
                .getOrElseThrow(() -> new AggregateRootNotFoundException("Cannot find order with id: " + orderId.getId()));
    }

    private Consumer find(ConsumerId consumerId) {
        return consumers.findBy(consumerId)
                .getOrElseThrow(() -> new AggregateRootNotFoundException(
                        "Consumer with the given id does not exists: " + consumerId.getId()));
    }
}
