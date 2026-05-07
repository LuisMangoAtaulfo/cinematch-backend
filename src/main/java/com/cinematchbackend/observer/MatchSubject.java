package com.cinematchbackend.observer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchSubject {

    private final List<MatchObserver> observers;

    public void notificarObservers(Long salaId, String contenidoId) {
        observers.forEach(o -> o.notificar(salaId, contenidoId));
    }
}