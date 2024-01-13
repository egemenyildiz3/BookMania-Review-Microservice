package nl.tudelft.sem.template.review.services;

import java.util.List;
import nl.tudelft.sem.template.model.ReportComment;
import org.springframework.http.ResponseEntity;


public interface ReportCommentService {
    ResponseEntity<ReportComment> report(Long commentId, String reason);

    ResponseEntity<ReportComment> get(Long id);

    ResponseEntity<List<ReportComment>> getReportsForComment(Long commentId);

    ResponseEntity<List<ReportComment>> getAllReportedComments(Long userId);

    ResponseEntity<Boolean> isReported(Long commentId);

    ResponseEntity<String> delete(Long id, Long userId);

    //ResponseEntity<String> deleteReportsForComment(Long commentId, Long userId);
}

