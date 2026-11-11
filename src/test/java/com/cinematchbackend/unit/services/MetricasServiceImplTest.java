package com.cinematchbackend.unit.services;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.dto.response.MetricasUsuarioResponseDTO;
import com.cinematchbackend.entities.CalificacionEntidad;
import com.cinematchbackend.entities.MatchEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.repositories.EvaluacionRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.impl.MetricasServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricasServiceImplTest {

    @Mock
    private SalaRepository salaRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @InjectMocks
    private MetricasServiceImpl metricasService;

    @BeforeEach
    void setUp() {
        // Configuración común para cada test
    }

    @Test
    void procesarMetricas_CuandoNoHayDatos_DeberiaRetornarCeros() {
        // Arrange
        when(salaRepository.count()).thenReturn(0L);
        when(salaRepository.countByEstado(any())).thenReturn(0L);
        when(matchRepository.count()).thenReturn(0L);
        when(calificacionRepository.calcularPromedio()).thenReturn(null);

        // Act
        MetricasResponseDTO resultado = metricasService.procesarMetricas();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalSalas()).isEqualTo(0L);
        assertThat(resultado.getSalasActivas()).isEqualTo(0L);
        assertThat(resultado.getTotalMatches()).isEqualTo(0L);
        assertThat(resultado.getPromedioCalificacion()).isNull();
    }

    @Test
    void procesarMetricas_CuandoHayDatos_DeberiaRetornarMetricasCorrectas() {
        // Arrange
        when(salaRepository.count()).thenReturn(10L);
        when(salaRepository.countByEstado(EstadoSala.ACTIVA)).thenReturn(3L);
        when(matchRepository.count()).thenReturn(25L);
        when(calificacionRepository.calcularPromedio()).thenReturn(4.5);

        // Act
        MetricasResponseDTO resultado = metricasService.procesarMetricas();

        // Assert
        assertThat(resultado.getTotalSalas()).isEqualTo(10L);
        assertThat(resultado.getSalasActivas()).isEqualTo(3L);
        assertThat(resultado.getTotalMatches()).isEqualTo(25L);
        assertThat(resultado.getPromedioCalificacion()).isEqualTo(4.5);
    }

    @Test
    void procesarMetricasUsuario_CuandoUsuarioNoTieneActividad_DeberiaRetornarCeros() {
        // Arrange
        Long usuarioId = 1L;
        when(salaRepository.findAllByUsuario(usuarioId)).thenReturn(Collections.emptyList());
        when(salaRepository.countByEstadoAndUsuario(any(), eq(usuarioId))).thenReturn(0L);
        when(matchRepository.countByUsuario(usuarioId)).thenReturn(0L);
        when(calificacionRepository.calcularPromedioByUsuario(usuarioId)).thenReturn(null);
        when(evaluacionRepository.countByUsuarioId(usuarioId)).thenReturn(0L);

        // Act
        MetricasUsuarioResponseDTO resultado = metricasService.procesarMetricasUsuario(usuarioId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalSalas()).isEqualTo(0L);
        assertThat(resultado.getSalasActivas()).isEqualTo(0L);
        assertThat(resultado.getTotalMatches()).isEqualTo(0L);
        assertThat(resultado.getPromedioCalificacion()).isEqualTo(0.0);
        assertThat(resultado.getTotalEvaluaciones()).isEqualTo(0L);
    }

    @Test
    void procesarMetricasUsuario_CuandoUsuarioTieneActividad_DeberiaRetornarMetricasCorrectas() {
        // Arrange
        Long usuarioId = 1L;
        SalaEntidad sala1 = new SalaEntidad();
        sala1.setEstado(EstadoSala.ACTIVA);
        SalaEntidad sala2 = new SalaEntidad();
        sala2.setEstado(EstadoSala.FINALIZADA);

        when(salaRepository.findAllByUsuario(usuarioId)).thenReturn(List.of(sala1, sala2));
        when(salaRepository.countByEstadoAndUsuario(EstadoSala.ACTIVA, usuarioId)).thenReturn(1L);
        when(matchRepository.countByUsuario(usuarioId)).thenReturn(5L);
        when(calificacionRepository.calcularPromedioByUsuario(usuarioId)).thenReturn(4.2);
        when(evaluacionRepository.countByUsuarioId(usuarioId)).thenReturn(10L);

        // Act
        MetricasUsuarioResponseDTO resultado = metricasService.procesarMetricasUsuario(usuarioId);

        // Assert
        assertThat(resultado.getTotalSalas()).isEqualTo(2L);
        assertThat(resultado.getSalasActivas()).isEqualTo(1L);
        assertThat(resultado.getTotalMatches()).isEqualTo(5L);
        assertThat(resultado.getPromedioCalificacion()).isEqualTo(4.2);
        assertThat(resultado.getTotalEvaluaciones()).isEqualTo(10L);
    }

    @Test
    void obtenerHistorial_CuandoNoHayFechas_DeberiaRetornarListaVacia() {
        // Arrange
        when(salaRepository.findAllFechasCreacion()).thenReturn(Collections.emptyList());

        // Act
        List<MetricasHistorialResponseDTO> resultado = metricasService.obtenerHistorial();

        // Assert
        assertThat(resultado).isEmpty();
    }
}
