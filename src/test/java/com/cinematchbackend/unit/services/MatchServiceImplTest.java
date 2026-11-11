package com.cinematchbackend.unit.services;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.MatchEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.enums.TipoContenido;
import com.cinematchbackend.exceptions.ContenidoNoEncontradoException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.impl.MatchServiceImpl;
import com.cinematchbackend.services.interfaces.EventPublisherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Test
    void buscarMatches_CuandoNoHayMatches_DeberiaRetornarListaVacia() {
        // Arrange
        Long salaId = 1L;
        when(matchRepository.findBySalaId(salaId)).thenReturn(Collections.emptyList());

        // Act
        List<MatchResponseDTO> resultado = matchService.buscarMatches(salaId);

        // Assert
        assertThat(resultado).isEmpty();
        verify(matchRepository, times(1)).findBySalaId(salaId);
    }

    @Test
    void buscarMatches_CuandoHayMatches_DeberiaRetornarListaMapeada() {
        // Arrange
        Long salaId = 1L;
        SalaEntidad sala = new SalaEntidad();
        sala.setId(salaId);
        
        ContenidoEntidad contenido = new ContenidoEntidad();
        contenido.setContenidoId("tmdb-123");
        contenido.setTitulo("Test Movie");
        contenido.setAnio(2024);
        contenido.setTipo(TipoContenido.PELICULA);
        
        MatchEntidad match = new MatchEntidad();
        match.setId(1L);
        match.setSala(sala);
        match.setContenido(contenido);

        when(matchRepository.findBySalaId(salaId)).thenReturn(List.of(match));

        // Act
        List<MatchResponseDTO> resultado = matchService.buscarMatches(salaId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getSalaId()).isEqualTo(salaId);
        assertThat(resultado.get(0).getContenido().getTitulo()).isEqualTo("Test Movie");
    }

    @Test
    void registrarMatch_CuandoSalaNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        Long salaId = 999L;
        String contenidoId = "tmdb-123";
        when(salaRepository.findById(salaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> matchService.registrarMatch(salaId, contenidoId))
                .isInstanceOf(SalaNoEncontradaException.class)
                .hasMessageContaining("Sala no encontrada");
    }

    @Test
    void registrarMatch_CuandoContenidoNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        Long salaId = 1L;
        String contenidoId = "tmdb-999";
        SalaEntidad sala = new SalaEntidad();
        sala.setId(salaId);
        
        when(salaRepository.findById(salaId)).thenReturn(Optional.of(sala));
        when(contenidoRepository.findById(contenidoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> matchService.registrarMatch(salaId, contenidoId))
                .isInstanceOf(ContenidoNoEncontradoException.class)
                .hasMessageContaining("Contenido no encontrado");
    }

    @Test
    void registrarMatch_CuandoTodoExiste_DeberiaCrearMatchYPublicarEvento() {
        // Arrange
        Long salaId = 1L;
        String contenidoId = "tmdb-123";
        
        SalaEntidad sala = new SalaEntidad();
        sala.setId(salaId);
        
        ContenidoEntidad contenido = new ContenidoEntidad();
        contenido.setContenidoId(contenidoId);
        contenido.setTitulo("Test Movie");
        
        MatchEntidad matchGuardado = new MatchEntidad();
        matchGuardado.setId(1L);
        matchGuardado.setSala(sala);
        matchGuardado.setContenido(contenido);

        when(salaRepository.findById(salaId)).thenReturn(Optional.of(sala));
        when(contenidoRepository.findById(contenidoId)).thenReturn(Optional.of(contenido));
        when(matchRepository.save(any(MatchEntidad.class))).thenReturn(matchGuardado);

        // Act
        MatchEntidad resultado = matchService.registrarMatch(salaId, contenidoId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(matchRepository, times(1)).save(any(MatchEntidad.class));
        verify(eventPublisherService, times(1)).publicarMatch(eq(salaId), any(MatchResponseDTO.class));
    }
}
