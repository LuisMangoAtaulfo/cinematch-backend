package com.cinematchbackend.regression;

import com.cinematchbackend.CinematchBackendApplication;
import org.junit.jupiter.api.Tag;
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
 * Pruebas de Regresión - Aseguran que cambios no rompan funcionalidad existente.
 * Etiquetadas con @Tag("regression") para ejecución selectiva.
 */
@SpringBootTest(classes = CinematchBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("regression")
class RegresionGraphQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void regresionMetricas_CuandoSeConsulta_DeberiaMantenerCompatibilidad() throws Exception {
        // Regresión: asegurar que la query metricas sigue funcionando
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
    void regresionHistorialMetricas_CuandoSeConsulta_DeberiaMantenerCompatibilidad() throws Exception {
        // Regresión: asegurar que historialMetricas sigue funcionando
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
    void regresionMetricasUsuario_CuandoSeConsulta_DeberiaMantenerCompatibilidad() throws Exception {
        // Regresión: asegurar que metricasUsuario(id) sigue funcionando
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
    void regresionHistorialMetricasUsuario_CuandoSeConsulta_DeberiaMantenerCompatibilidad() throws Exception {
        // Regresión: asegurar que historialMetricasUsuario(id) sigue funcionando
        String query = """
            {
                "query": "{ historialMetricasUsuario(id: 1) { fecha totalSalas salasActivas totalMatches promedioCalificacion } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk());
    }
}
