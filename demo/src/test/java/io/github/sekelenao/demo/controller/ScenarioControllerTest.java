package io.github.sekelenao.demo.controller;

import io.github.sekelenao.demo.model.Scenario;
import io.github.sekelenao.demo.scenario.ScenarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ScenarioController.class)
class ScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScenarioService scenarioService;

    @Test
    @DisplayName("Should display index view with scenarios in model")
    void shouldDisplayIndexPageWithScenarios() throws Exception {
        Scenario scenario = new Scenario("showcase-demo", "Showcase demo", List.of());
        when(scenarioService.listScenarios()).thenReturn(List.of(scenario));

        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"))
            .andExpect(model().attributeExists("scenarios"));
    }

    @Test
    @DisplayName("Should run scenario and redirect with success flash message")
    void shouldRunScenarioAndRedirectWithSuccessMessage() throws Exception {
        mockMvc.perform(post("/scenarios/{name}/run", "showcase-demo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("successMessage"));

        verify(scenarioService).runScenario("showcase-demo");
    }

    @Test
    @DisplayName("Should run scenario from form param and redirect with success flash message")
    void shouldRunScenarioFromFormAndRedirectWithSuccessMessage() throws Exception {
        mockMvc.perform(post("/scenarios/run").param("name", "showcase-demo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("successMessage"));

        verify(scenarioService).runScenario("showcase-demo");
    }

    @Test
    @DisplayName("Should handle scenario execution error and redirect with error flash message")
    void shouldHandleRunScenarioErrorAndRedirectWithErrorMessage() throws Exception {
        doThrow(new RuntimeException("Kafka error")).when(scenarioService).runScenario("showcase-demo");

        mockMvc.perform(post("/scenarios/{name}/run", "showcase-demo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"))
            .andExpect(flash().attributeExists("errorMessage"));
    }
}
