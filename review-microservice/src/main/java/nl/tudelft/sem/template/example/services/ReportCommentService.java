package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ReportCommentService {
    ResponseEntity<ReportComment> report(Comment comment, String reason);

    ResponseEntity<ReportComment> get(Long id);

    ResponseEntity<List<ReportComment>> getReportsForComment(Long commentId);

    ResponseEntity<List<ReportComment>> getAllReportedComments(Long userId);

    ResponseEntity<Boolean> isReported(Long commentId);

    ResponseEntity<String> delete(Long id, Long userId);

    ResponseEntity<String> deleteReportsForComment(Long commentId, Long userId);
}

