package com.cinematchbackend.config;

import com.cinematchbackend.exceptions.*;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        // R-BE-01: Validación de campos vacíos
        if (ex instanceof CamposVaciosException) {
            return GraphQLError.newError()
                    .message("Completa todos los campos")
                    .build();
        }

        // R-BE-01: Validación de nombre requerido
        if (ex instanceof NombreRequeridoException) {
            return GraphQLError.newError()
                    .message("El nombre es requerido")
                    .build();
        }

        // R-BE-02: Autenticación requerida
        if (ex instanceof UnauthorizedException) {
            return GraphQLError.newError()
                    .message("Autenticación requerida")
                    .build();
        }

        // R-BE-02/03: Permisos insuficientes
        if (ex instanceof ForbiddenException) {
            return GraphQLError.newError()
                    .message("No tienes permisos para realizar esta acción")
                    .build();
        }

        if (ex instanceof UsuarioNoEncontradoException) {
            return GraphQLError.newError().message("Usuario no encontrado").build();
        }

        if (ex instanceof BadCredentialsException) {
            return GraphQLError.newError().message("Credenciales incorrectas").build();
        }

        if (ex instanceof EmailAlreadyExistsException) {
            return GraphQLError.newError().message("El correo ya está en uso").build();
        }

        if (ex instanceof SalaNoEncontradaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        // R-BE-01: Código de sala inválido
        if (ex instanceof CodigoSalaInvalidoException) {
            return GraphQLError.newError().message("Código de sala inválido").build();
        }

        if (ex instanceof SalaActivaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof AuthorizationException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof ValidationException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof CalificacionInvalidaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof CalificacionDuplicadaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof SalaNoFinalizadaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof EvaluacionDuplicadaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof ContenidoNoEncontradoException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof PlataformaNoEncontradaException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof UsuarioDesconectadoException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        if (ex instanceof TestModeNotEnabledException) {
            return GraphQLError.newError().message(ex.getMessage()).build();
        }

        return null;
    }
}
