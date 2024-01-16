package nl.tudelft.sem.template.review.services;


import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ReportCommentServiceImplTest {

    private ReportCommentServiceImpl service;
    private ReportCommentRepository repository;
    private CommentRepository commentRepository;
    private CommunicationServiceImpl communicationService;

    /**
     * Sets up the necessary mocks before each individual test.
     */
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
        Comment mockComment = new Comment(null, null, null,null);
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
    void reportInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;
        long validCommentId = 1L;
        String validReason = "Inappropriate content";
        long validReviewId = 30L;

        Comment mockComment = new Comment(null, null, null,null);
        mockComment.setId(validCommentId);
        mockComment.setUserId(123L);
        mockComment.setReviewId(validReviewId);

        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(commentRepository.getOne(validCommentId)).thenReturn(mockComment);
        when(communicationService.existsUser(mockComment.getUserId())).thenReturn(true);
        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.report(invalidCommentId, validReason));
        assertThrows(CustomBadRequestException.class, () -> service.report(null, "Reason"));
        assertThrows(CustomBadRequestException.class, () -> service.report(1L, null));

    }

    @Test
    void getValid() {
        long validReportId = 1L;
        ReportComment mockReportComment = new ReportComment(null,null,null);
        mockReportComment.setId(validReportId);

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(mockReportComment));

        ResponseEntity<ReportComment> result = service.get(validReportId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(validReportId, result.getBody().getId());
    }

    @Test
    void getInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;
        long validCommentId = 1L;
        ReportComment mockReportComment = new ReportComment(null,null,null);
        mockReportComment.setCommentId(validCommentId);

        when(repository.existsById(invalidReportId)).thenReturn(false);
        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(repository.findAllByCommentId(validCommentId)).thenReturn(Collections.singletonList(mockReportComment));

        ResponseEntity<List<ReportComment>> result = service.getReportsForComment(validCommentId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(validCommentId, result.getBody().get(0).getCommentId());
        assertThrows(CustomBadRequestException.class, () -> service.get(invalidReportId));
    }
    @Test
    void getReportsForCommentValid() {
        long validCommentId = 1L;
        ReportComment mockReportComment = new ReportComment(null,null,null);
        mockReportComment.setCommentId(validCommentId);

        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(repository.findAllByCommentId(validCommentId)).thenReturn(Collections.singletonList(mockReportComment));

        ResponseEntity<List<ReportComment>> result = service.getReportsForComment(validCommentId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(validCommentId, result.getBody().get(0).getCommentId());
    }
    @Test
    void getReportsForCommentInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.getReportsForComment(invalidCommentId));
    }

    @Test
    void getAllReportedCommentsValid() {
        long validUserId = 1L;
        ReportComment mockReportComment = new ReportComment(null,null,null);
        mockReportComment.setCommentId(validUserId);

        when(communicationService.isAdmin(validUserId)).thenReturn(true);
        when(repository.findAll()).thenReturn(Collections.singletonList(mockReportComment));

        ResponseEntity<List<ReportComment>> result = service.getAllReportedComments(validUserId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(validUserId, result.getBody().get(0).getCommentId());
    }

    @Test
    void getAllReportedCommentsInvalidThrowsPermissionsException() {
        long nonAdminUserId = 456L;

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.getAllReportedComments(nonAdminUserId));
    }

    @Test
    void isReportedValid() {
        long validCommentId = 1L;
        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(repository.existsByCommentId(validCommentId)).thenReturn(true);

        ResponseEntity<Boolean> result = service.isReported(validCommentId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody());
    }
    @Test
    void isReportedValidThrowsBadRequestException() {
        long invalidCommentId = 999L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.isReported(invalidCommentId));
    }
    @Test
    void deleteValid() {
        long validCommentId = 1L;
        long adminUserId = 123L;
        ReportComment mockReportComment = new ReportComment(null,null,null);
        mockReportComment.setId(validCommentId);
        mockReportComment.setCommentId(456L);
        Comment com = new Comment(null, null, null,null);
        com.setReportList(new ArrayList<>());
        when(repository.existsById(validCommentId)).thenReturn(true);
        when(repository.findById(validCommentId)).thenReturn(Optional.of(mockReportComment));
        when(communicationService.isAdmin(adminUserId)).thenReturn(true);
        when(commentRepository.getOne(mockReportComment.getCommentId())).thenReturn(com);

        ResponseEntity<String> result = service.delete(validCommentId, adminUserId);

        assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    void deleteInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;
        long adminUserId = 123L;

        when(repository.existsById(invalidReportId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.delete(invalidReportId, adminUserId));
    }
    @Test
    void deleteInvalidThrowsPermissionsException() {
        long validReportId = 1L;
        long nonAdminUserId = 456L;

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(new ReportComment(null,null,null)));
        when(communicationService.existsUser(nonAdminUserId)).thenReturn(true);

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.delete(validReportId, nonAdminUserId));
    }
    @Test
    void deleteReportsForCommentValid() {
        long validCommentId = 1L;
        long adminUserId = 123L;

        Comment com = new Comment(null, null, null,null);
        com.setReportList(new ArrayList<>());
        when(commentRepository.existsById(validCommentId)).thenReturn(true);
        when(commentRepository.getOne(validCommentId)).thenReturn(com);
        when(communicationService.isAdmin(adminUserId)).thenReturn(true);

        ResponseEntity<String> result = service.deleteReportsForComment(validCommentId, adminUserId);

        assertEquals(200, result.getStatusCodeValue());
    }
    @Test
    void deleteReportsForCommentInvalidThrowsBadRequestException() {
        long invalidCommentId = 999L;
        long adminUserId = 123L;

        when(commentRepository.existsById(invalidCommentId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.deleteReportsForComment(invalidCommentId, adminUserId));
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
