package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


//take a look at the changes I made to the reportreview tests and try to do the same here.
// The main issues was you forgot to mock the repo. Now there are some more when statements to write.
@RunWith(MockitoJUnitRunner.class)
class ReportCommentServiceImplTest {

    private ReportCommentServiceImpl service;

    @Mock
    private ReportCommentRepository repository;

    @BeforeEach
    public void setup() {
        repository = mock(ReportCommentRepository.class);
        service = new ReportCommentServiceImpl(repository);
    }

    @Test
    void report() {
        Comment comment = new Comment(1L, 223L, 33L);
        when(repository.save(any())).thenReturn(new ReportComment());

        ResponseEntity<ReportComment> result = service.report(comment);

        verify(repository).save(any());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void get() {
        ReportComment reportComment = new ReportComment();
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(reportComment));

        ResponseEntity<ReportComment> result = service.get(1L);

        verify(repository).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(reportComment, result.getBody());
    }

    @Test
    void reportInvalid() {
        ResponseEntity<ReportComment> result = service.report(null);

        verify(repository, never()).save(any());
        verify(repository).existsById(0L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void getInvalid() {
        ResponseEntity<ReportComment> result = service.get(0L);

        verify(repository, never()).findById(0L);
        verify(repository, never()).existsById(0L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void getReportsForComment() {
        List<ReportComment> reports = Arrays.asList(new ReportComment(), new ReportComment());
        when(repository.findAllByCommentId(321L)).thenReturn(reports);

        ResponseEntity<List<ReportComment>> result = service.getReportsForComment(321L);

        verify(repository).findAllByCommentId(321L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(reports, result.getBody());
    }

    @Test
    void getAllReportedCommentsValid() {
        when(repository.findAll()).thenReturn(Arrays.asList(new ReportComment(), new ReportComment()));

        ResponseEntity<List<ReportComment>> result = service.getAllReportedComments(11L);

        verify(repository).findAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void getAllReportedCommentsInvalid() {
        ResponseEntity<List<ReportComment>> result = service.getAllReportedComments(0L);

        verify(repository, never()).findAll();
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void isReported() {
        when(repository.existsByCommentId(1L)).thenReturn(true);

        ResponseEntity<Boolean> result = service.isReported(1L);

        verify(repository).existsByCommentId(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }

    @Test
    void isNotReported() {
        ResponseEntity<Boolean> result = service.isReported(0L);

        verify(repository).existsByCommentId(0L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody());
    }

    @Test
    void delete() {
        ReportComment reportComment = new ReportComment();
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(reportComment));

        ResponseEntity<String> result = service.delete(1L, 1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteInvalid() {
        ResponseEntity<String> result = service.delete(0L, 1L);

        verify(repository).existsById(0L);
        verify(repository, never()).deleteById(0L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void deleteReportsForReview() {
        when(repository.existsByCommentId(1L)).thenReturn(true);
        when(repository.findAllByCommentId(1L)).thenReturn(Arrays.asList(new ReportComment()));
    }

    @Test
    void deleteReportsForCommentInvalid() {
        ResponseEntity<String> result = service.deleteReportsForComment(0L, 1L);

        verify(repository, never()).existsByCommentId(0L);
        verify(repository, never()).findAllByCommentId(0L);
        verify(repository, never()).delete(any());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

/*    @Test
    void deleteReportsForCommentNotAdmin() {
        when(repository.existsByCommentId(1L)).thenReturn(true);

        ResponseEntity<String> result = service.deleteReportsForComment(1L, 0L);

        verify(repository).existsByCommentId(1L);
        verify(repository, never()).findAllByCommentId(1L);
        verify(repository, never()).delete(any());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

 */
}
