package com.cinematchbackend.system;

import com.cinematchbackend.CinematchBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de Sistema - Evalúan la aplicación completa e integrada.
 * Verifican flujos end-to-end con el contexto completo de Spring.
 */
@SpringBootTest(classes = CinematchBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SistemaEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void flujoCompletoMetricas_CuandoSeConsulta_DeberiaResponderCorrectamente() throws Exception {
        // Escenario: consulta de métricas globales
        String query = """
            {
                "query": "{ metricas { totalSalas salasActivas totalMatches promedioCalificacion } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk());
    }

    @Test
    void flujoCompletoMetricasUsuario_CuandoSeConsultaConId_DeberiaResponderCorrectamente() throws Exception {
        // Escenario: consulta de métricas de un usuario específico
        String query = """
            {
                "query": "{ metricasUsuario(id: 1) { totalSalas salasActivas totalMatches promedioCalificacion totalEvaluaciones } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk());
    }

    @Test
    void flujoCompletoHistorial_CuandoSeConsulta_DeberiaResponderCorrectamente() throws Exception {
        // Escenario: consulta de historial de métricas
        String query = """
            {
                "query": "{ historialMetricas { fecha totalSalas salasActivas totalMatches promedioCalificacion } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk());
    }

    @Test
    void seguridad_CuandoSeAccedeSinAutenticacion_DeberiaPermitirAccesoAGraphQL() throws Exception {
        // Escenario: GraphQL está configurado como permitAll en SecurityConfig
        String query = """
            {
                "query": "{ metricas { totalSalas } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk());
    }
}
