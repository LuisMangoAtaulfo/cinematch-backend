package com.cinematchbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;

/**
 * Interceptor WebSocket para GraphQL que maneja la autenticación JWT
 * durante la inicialización de la conexión WebSocket.
 * 
 * El cliente debe enviar el token en connectionParams:
 * {
 *   "authToken": "jwt-token-aqui"
 * }
 */
@Component
@RequiredArgsConstructor
public class GraphQLWebSocketAuthInterceptor implements WebSocketGraphQlInterceptor {

    private static final String AUTH_ATTRIBUTE = "AUTHENTICATION";

    private final JwtService jwtService;

    @Override
    public Mono<Object> handleConnectionInitialization(
            WebSocketSessionInfo sessionInfo,
            Map<String, Object> connectionInitPayload) {
        
        Object authToken = connectionInitPayload.get("authToken");
        if (authToken == null) {
            return Mono.error(new RuntimeException("No authentication token provided"));
        }

        String token = authToken.toString();

        if (!jwtService.esValido(token)) {
            return Mono.error(new RuntimeException("Invalid authentication token"));
        }

        String correo = jwtService.extraerCorreo(token);
        String rol = jwtService.extraerRol(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                correo,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );

        // Guardar la autenticación en los atributos de la sesión WebSocket
        sessionInfo.getAttributes().put(AUTH_ATTRIBUTE, authentication);

        return Mono.just(Map.of("status", "success"));
    }

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, WebGraphQlInterceptor.Chain chain) {
        Authentication authentication = (Authentication) request.getAttributes().get(AUTH_ATTRIBUTE);
        if (authentication == null) {
            return chain.next(request);
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        // El SecurityContext va al Reactor Context (y no solo al ThreadLocal) porque
        // Spring GraphQL captura el ContextSnapshot ANTES de ejecutar este interceptor:
        // al setear solo el ThreadLocal, ContextDataFetcherDecorator.snapshot.wrap()
        // lo pisaba con el snapshot vacío justo antes de invocar el resolver y
        // @PreAuthorize fallaba con "Access Denied" aunque el token fuera válido.
        // La clave debe coincidir con SecurityContextHolderThreadLocalAccessor.key()
        // para que context-propagation restaure el contexto en el hilo del resolver.
        return chain.next(request)
                .contextWrite(ctx -> ctx.put(SecurityContext.class.getName(), securityContext));
    }
}
