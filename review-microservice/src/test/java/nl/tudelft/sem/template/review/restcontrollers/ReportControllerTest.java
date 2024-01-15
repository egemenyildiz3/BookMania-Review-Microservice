/*
package nl.tudelft.sem.template.review.restcontrollers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.restcontrollers.ReportController;
import nl.tudelft.sem.template.review.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.review.services.ReportReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {
    @Mock
    ReportReviewServiceImpl reportReviewService;

    @Mock
    ReportCommentServiceImpl reportCommentService;

    @InjectMocks
    ReportController controller;

    @Test
    void reportReviewTest() throws Exception {
        ReportReview reportReview = new ReportReview();
        reportReview.setReviewId(1L);
        reportReview.setReason("body");

        when(reportReviewService.report(1L, "body")).thenReturn(ResponseEntity.ok(reportReview));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String json = objectMapper.writeValueAsString(reportReview);

        mockMvc.perform(post("/report/review/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reportReviewService, times(1)).report(1L, "body");
    }

    @Test
    void reportCommentTest() throws Exception {
        ReportComment reportComment = new ReportComment();
        reportComment.setCommentId(1L);
        reportComment.setReason("body");

        when(reportCommentService.report(1L, "body")).thenReturn(ResponseEntity.ok(reportComment));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String json = objectMapper.writeValueAsString(reportComment);

        mockMvc.perform(post("/report/comment/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reportCommentService, times(1)).report(1L, "body");
    }

    @Test
    void deleteReview() throws Exception {
        when(reportReviewService.delete(1L, 1L)).thenReturn(ResponseEntity.ok("Deleted"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(delete("/report/review/{reportId}/{userId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reportReviewService, times(1)).delete(1L, 1L);

    }

    @Test
    void deleteComment() throws Exception {
        when(reportCommentService.delete(1L, 1L)).thenReturn(ResponseEntity.ok("Deleted"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(delete("/report/comment/{reportId}/{userId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reportCommentService, times(1)).delete(1L, 1L);

    }

}

 */

