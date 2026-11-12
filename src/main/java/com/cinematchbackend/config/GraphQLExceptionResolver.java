package com.cinematchbackend.config;

import com.cinematchbackend.exceptions.*;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // R-BE-01: Validación de campos vacíos
        if (ex instanceof CamposVaciosException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("Completa todos los campos")
                    .build();
        }

        // R-BE-01: Validación de nombre requerido
        if (ex instanceof NombreRequeridoException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("El nombre es requerido")
                    .build();
        }

        // R-BE-02: Autenticación requerida
        if (ex instanceof UnauthorizedException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("Autenticación requerida")
                    .build();
        }

        // R-BE-02/03: Permisos insuficientes
        if (ex instanceof ForbiddenException) {
            return GraphqlErrorBuilder.newError(env)
                    .message("No tienes permisos para realizar esta acción")
                    .build();
        }

        if (ex instanceof UsuarioNoEncontradoException) {
            return GraphqlErrorBuilder.newError(env).message("Usuario no encontrado").build();
        }

        if (ex instanceof BadCredentialsException) {
            return GraphqlErrorBuilder.newError(env).message("Credenciales incorrectas").build();
        }

        if (ex instanceof EmailAlreadyExistsException) {
            return GraphqlErrorBuilder.newError(env).message("El correo ya está en uso").build();
        }

        if (ex instanceof SalaNoEncontradaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        // R-BE-01: Código de sala inválido
        if (ex instanceof CodigoSalaInvalidoException) {
            return GraphqlErrorBuilder.newError(env).message("Código de sala inválido").build();
        }

        if (ex instanceof SalaActivaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof AuthorizationException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof ValidationException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof CalificacionInvalidaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof CalificacionDuplicadaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof SalaNoFinalizadaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof EvaluacionDuplicadaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof ContenidoNoEncontradoException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof PlataformaNoEncontradaException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof UsuarioDesconectadoException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        if (ex instanceof TestModeNotEnabledException) {
            return GraphqlErrorBuilder.newError(env).message(ex.getMessage()).build();
        }

        return null;
    }
}
