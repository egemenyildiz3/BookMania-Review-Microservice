package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.services.ReportCommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReportCommentServiceImplTest {

    private ReportCommentServiceImpl service;
    private ReportCommentRepository repository;
    private CommentRepository commentRepository;
    private CommunicationServiceImpl communicationService;

    @BeforeEach
    public void setup() {
        repository = mock(ReportCommentRepository.class);
        commentRepository = mock(CommentRepository.class);
        communicationService = mock(CommunicationServiceImpl.class);
        service = new ReportCommentServiceImpl(repository, communicationService, commentRepository);
    }

    @Test
    void reportValid() {
        long validCommentId = 1L;
        String validReason = "Inappropriate content";
        Comment mockComment = new Comment();
        mockComment.setId(validCommentId);
        mockComment.setReviewId(30L);
        mockComment.setText("This is a valid review text.");
        mockComment.setUserId(123L);

        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(commentRepository.getOne(validCommentId)).thenReturn(mockComment);
        when(communicationService.existsUser(mockComment.getUserId())).thenReturn(true);

        ResponseEntity<ReportComment> result = service.report(validCommentId, validReason);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(validReason, result.getBody().getReason());
        assertEquals(validCommentId, result.getBody().getCommentId());
    }

    @Test
    void getValid() {
        long validReportId = 1L;
        ReportComment mockReportComment = new ReportComment();
        mockReportComment.setId(validReportId);

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(mockReportComment));

        ResponseEntity<ReportComment> result = service.get(validReportId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(validReportId, result.getBody().getId());
    }

    @Test
    void reportInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;
        String validReason = "Inappropriate content";

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.report(invalidCommentId, validReason));
    }

    @Test
    void getInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;

        when(repository.existsById(invalidReportId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.get(invalidReportId));
    }

    @Test
    void getReportsForCommentInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.getReportsForComment(invalidCommentId));
    }

    @Test
    void isReportedValidThrowsBadRequestException() {
        long invalidCommentId = 999L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.isReported(invalidCommentId));
    }

    @Test
    void deleteInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;
        long adminUserId = 123L;

        when(repository.existsById(invalidReportId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.delete(invalidReportId, adminUserId));
    }

    @Test
    void deleteReportsForCommentInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;
        long adminUserId = 123L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.deleteReportsForComment(invalidCommentId, adminUserId));
    }

    @Test
    void getAllReportedCommentsInvalidThrowsPermissionsException() {
        long nonAdminUserId = 456L;

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.getAllReportedComments(nonAdminUserId));
    }

    @Test
    void deleteInvalidThrowsPermissionsException() {
        long validReportId = 1L;
        long nonAdminUserId = 456L;

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(new ReportComment()));
        when(communicationService.existsUser(nonAdminUserId)).thenReturn(true);

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.delete(validReportId, nonAdminUserId));
    }

    @Test
    void deleteReportsForCommentInvalidThrowsPermissionsException() {
        long validCommentId = 1L;
        long nonAdminUserId = 456L;

        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.deleteReportsForComment(validCommentId, nonAdminUserId));
    }

}
