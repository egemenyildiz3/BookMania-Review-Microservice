package nl.tudelft.sem.template.review.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.review.services.CommentServiceImpl;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(MockitoJUnitRunner.class)
class CommentControllerTest {
    @Mock
    CommentServiceImpl service;

    @InjectMocks
    CommentController controller;

    @Test
    void add() throws Exception {
        Comment com = new Comment(1L, 2L, 5L, "wow");
        when(service.add(com)).thenReturn(ResponseEntity.ok(com));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        String json = objectMapper.writeValueAsString(com);


        mvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).add(com);
    }

    @Test
    void getTest() throws Exception {
        Comment com = new Comment(1L, 2L, 10L, "wow");
        when(service.get(1L)).thenReturn(ResponseEntity.ok(com));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/comment/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).get(1L);
    }

    @Test
    void deleteTest() throws Exception {
        when(service.delete(1L, 10L)).thenReturn(ResponseEntity.ok("deleted"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(delete("/comment/delete/1/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(service, times(1)).delete(1L, 10L);
    }

    @Test
    void updateTest() throws Exception {
        Comment com = new Comment(1L, 2L, 10L, "wow");
        when(service.update(1L, com)).thenReturn(ResponseEntity.ok(com));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        String json = objectMapper.writeValueAsString(com);


        mvc.perform(put("/comment/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).update(1L, com);
    }


    @Test
    void getAllTest() throws Exception {
        Comment com = new Comment(1L, 2L, 10L, "wow");
        when(service.getAll(1L)).thenReturn(ResponseEntity.ok(List.of(com)));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/comment/seeAll/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(service, times(1)).getAll(1L);
    }

    @Test
    void addVoteTest() throws Exception {
        when(service.addVote(1L, 1)).thenReturn(ResponseEntity.ok("voted"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(put("/comment/vote/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(service, times(1)).addVote(1L, 1);
    }

}