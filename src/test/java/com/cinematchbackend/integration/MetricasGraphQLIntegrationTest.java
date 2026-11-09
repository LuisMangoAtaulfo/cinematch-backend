package com.cinematchbackend.integration;

import com.cinematchbackend.CinematchBackendApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CinematchBackendApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MetricasGraphQLIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void consultarMetricas_CuandoSeLlama_DeberiaRetornarRespuestaOk() throws Exception {
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
    void consultarHistorialMetricas_CuandoSeLlama_DeberiaRetornarLista() throws Exception {
        String query = """
            {
                "query": "{ historialMetricas { fecha totalSalas salasActivas totalMatches promedioCalificacion } }"
            }
            """;

        mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.historialMetricas").isArray());
    }

    @Test
    void consultarMetricasUsuario_CuandoUsuarioExiste_DeberiaRetornarRespuestaOk() throws Exception {
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
}
