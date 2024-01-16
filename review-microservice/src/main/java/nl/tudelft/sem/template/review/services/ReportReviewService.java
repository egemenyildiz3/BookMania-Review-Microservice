package nl.tudelft.sem.template.review.services;

import java.util.List;
import nl.tudelft.sem.template.model.ReportReview;
import org.springframework.http.ResponseEntity;


public interface ReportReviewService {
    ResponseEntity<ReportReview> report(Long reviewId, String reason);

    ResponseEntity<ReportReview> get(Long id);

    ResponseEntity<List<ReportReview>> getReportsForReview(Long reviewId);

    ResponseEntity<List<ReportReview>> getAllReportedReviews(Long reviewId);

    ResponseEntity<Boolean> isReported(Long reviewId);

    ResponseEntity<String> delete(Long id, Long userId);

    ResponseEntity<String> deleteReportsForReview(Long reviewId, Long userId);
}

