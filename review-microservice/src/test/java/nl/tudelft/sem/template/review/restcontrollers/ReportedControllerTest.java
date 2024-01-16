package nl.tudelft.sem.template.review.restcontrollers;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportedControllerTest {
    @Mock
    ReportReviewServiceImpl reportReviewService;

    @Mock
    ReportCommentServiceImpl reportCommentService;

    @InjectMocks
    ReportedController controller;

    @Test
    void reportedReviewsTest() throws Exception {
        ReportReview reportReview = new ReportReview();
        reportReview.setReviewId(1L);
        reportReview.setReason("body");

        when(reportReviewService.getAllReportedReviews(anyLong())).thenReturn(ResponseEntity.ok(List.of(reportReview)));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/reported/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(reportReviewService, times(1)).getAllReportedReviews(anyLong());
    }


    @Test
    void reportedCommentsTest() throws Exception {
        ReportComment reportComment = new ReportComment();
        reportComment.setCommentId(1L);
        reportComment.setReason("body");

        when(reportCommentService.getAllReportedComments(anyLong())).thenReturn(ResponseEntity.ok(List.of(reportComment)));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/reported/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));


        verify(reportCommentService, times(1)).getAllReportedComments(anyLong());
    }
}