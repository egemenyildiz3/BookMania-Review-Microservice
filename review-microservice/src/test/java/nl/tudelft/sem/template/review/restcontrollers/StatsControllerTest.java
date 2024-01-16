package nl.tudelft.sem.template.review.restcontrollers;

import nl.tudelft.sem.template.review.services.StatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(MockitoJUnitRunner.class)
public class StatsControllerTest {

    @Mock
    StatsServiceImpl service;

    @InjectMocks
    StatsController controller;

    @Test
    public void testAvgRating() throws Exception {
        Double avgRating = 4.0d;

        when(service.avgRating(2L)).thenReturn(ResponseEntity.ok(avgRating));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/stats/avgRating/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).avgRating(2L);
    }

    @Test
    public void testInteractions() throws Exception {
        Long numInteractions = 68L;

        when(service.interactions(2L)).thenReturn(ResponseEntity.ok(numInteractions));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/stats/interactions/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).interactions(2L);
    }

    @Test
    public void testZeroInteractions() throws Exception {
        Long numInteractions = 0L;

        when(service.interactions(2L)).thenReturn(ResponseEntity.ok(numInteractions));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/stats/interactions/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).interactions(2L);
    }


    @Test
    public void testInteractionsBadBook() throws Exception {
        Long numInteractions = 0L;

        when(service.interactions(2L)).thenReturn(ResponseEntity.ok(numInteractions));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/stats/interactions/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).interactions(1L);
    }

}
