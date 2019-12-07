package com.gitlab.rstaskiewicz.tool.consumer.consumer;

import com.gitlab.rstaskiewicz.tool.consumer.common.aggregates.AggregateRootNotFoundException;
import com.gitlab.rstaskiewicz.tool.consumer.common.commands.Result;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacedEvents;
import com.gitlab.rstaskiewicz.tool.consumer.consumer.ConsumerEvent.OrderPlacingFailed;
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
public class PlacingOrder {

    private final Consumers consumers;

    public Try<Result> place(@NonNull PlaceOrderCommand command) {
        return Try.of(() -> {
            Consumer consumer = find(command.getConsumerId());
            Either<OrderPlacingFailed, OrderPlacedEvents> result = consumer.placeOrder(
                    command.getOrderId(), command.getSalesBranchId(), command.getPaymentDeadline());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents)
            );
        }).onFailure(ex -> log.error("Failed to place an order", ex));
    }

    private Result publishEvents(OrderPlacedEvents event) {
        consumers.publish(event);
        return Success;
    }

    private Result publishEvents(OrderPlacingFailed event) {
        consumers.publish(event);
        return Result.Rejection;
    }

    private Consumer find(ConsumerId consumerId) {
        return consumers.findBy(consumerId)
                .getOrElseThrow(() -> new AggregateRootNotFoundException(
                        "Consumer with the given id does not exists: " + consumerId.getId()));
    }
}
