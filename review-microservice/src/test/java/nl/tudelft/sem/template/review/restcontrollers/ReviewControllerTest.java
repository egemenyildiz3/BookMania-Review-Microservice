package nl.tudelft.sem.template.review.restcontrollers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.services.ReviewServiceImpl;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(MockitoJUnitRunner.class)
class ReviewControllerTest {
    @Mock
    ReviewServiceImpl service;

    @InjectMocks
    ReviewController controller;


    @Test
    void add() throws Exception {
        Review rev = new Review(1L, 2L, 10L, "wow", "review", 5L);
        when(service.add(rev)).thenReturn(ResponseEntity.ok(rev));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        String json = objectMapper.writeValueAsString(rev);


        mvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).add(rev);
    }

    @Test
    void getTest() throws Exception {
        Review rev = new Review(1L, 2L, 10L, "wow", "review", 5L);
        when(service.get(1L)).thenReturn(ResponseEntity.ok(rev));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(get("/review/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        //assertNotNull(mvcResult.getResponse());

        verify(service, times(1)).get(1L);
    }

    @Test
    void deleteTest() throws Exception {
        when(service.delete(1L, 10L)).thenReturn(ResponseEntity.ok("deleted"));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(delete("/review/delete/1/10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(service, times(1)).delete(1L, 10L);
    }

    @Test
    void updateTest() throws Exception {
        Review rev = new Review(1L, 2L, 10L, "wow", "review", 5L);
        when(service.update(1L, rev)).thenReturn(ResponseEntity.ok(rev));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        String json = objectMapper.writeValueAsString(rev);


        mvc.perform(put("/review/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).update(1L, rev);
    }

    @Test
    void spoilerTest() throws Exception {
        when(service.addSpoiler(1L)).thenReturn(ResponseEntity.ok("Spoiler added."));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();


        mvc.perform(put("/review/spoiler/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(true))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).addSpoiler(1L);
    }

    @Test
    void pinTest() throws Exception {
        when(service.pinReview(1L, true)).thenReturn(ResponseEntity.ok("Review pinned."));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();


        mvc.perform(put("/review/pin/1/true")

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).pinReview(1L, true);
    }


    @Test
    void seeAll() throws Exception {
        Review rev = new Review(1L, 2L, 10L, "wow", "review", 5L);
        Review rev2 = new Review(1L, 2L, 10L, "wow", "review", 5L);
        List<Review> reviews = List.of(rev, rev2);
        when(service.seeAll(2L, "mostRelevant")).thenReturn(ResponseEntity.ok(reviews));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();


        mvc.perform(get("/review/seeAll/2/mostRelevant")

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).seeAll(2L, "mostRelevant");
    }

    @Test
    void seeMostUpvoted() throws Exception {
        Review rev = new Review(1L, 2L, 10L, "wow", "review", 5L);
        Review rev2 = new Review(1L, 2L, 10L, "wow", "review", 5L);
        List<Review> reviews = List.of(rev, rev2);
        when(service.mostUpvotedReviews(10L)).thenReturn(ResponseEntity.ok(reviews));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();


        mvc.perform(get("/review/mostUpvoted/10")

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).mostUpvotedReviews(10L);
    }

    @Test
    void addVote() throws Exception {
        when(service.addVote(1L, 1)).thenReturn(ResponseEntity.ok("Voted added."));
        MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();


        mvc.perform(put("/review/vote/1/1")

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(service, times(1)).addVote(1L, 1);
    }


}