package com.cinematchbackend.config;

import graphql.GraphQLError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.SubscriptionExceptionResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Registra la excepción original de una suscripción GraphQL que falla.
 *
 * Sin este resolver, CompositeSubscriptionExceptionResolver devuelve el error
 * genérico "Subscription error" sin registrar la causa, y el fallo queda
 * invisible en los logs (fue el motivo por el que el bug de realtime no se
 * podía diagnosticar). Devolver vacío deja que el resolver por defecto
 * genere el error genérico para el cliente.
 */
@Component
@Slf4j
public class LoggingSubscriptionExceptionResolver implements SubscriptionExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable exception) {
        log.error("Suscripción GraphQL falló: {}", exception.getMessage(), exception);
        return Mono.empty();
    }
}
