package nl.tudelft.sem.template.review.restcontrollers;

import nl.tudelft.sem.template.api.ReportApi;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.review.services.ReportReviewServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController implements ReportApi {
    private final ReportReviewServiceImpl reportReviewService;
    private final ReportCommentServiceImpl reportCommentService;

    public ReportController(ReportReviewRepository reportReviewRepository, ReviewRepository reviewRepo,
                            ReportCommentRepository reportCommentRepository, CommentRepository commentRepository) {
        this.reportReviewService = new ReportReviewServiceImpl(reportReviewRepository, reviewRepo);
        this.reportCommentService = new ReportCommentServiceImpl(reportCommentRepository, commentRepository);
    }

    @Override
    public ResponseEntity<String> reportReviewDeleteReportIdUserIdDelete(Long userId, Long reportId) {
        return reportReviewService.delete(userId, reportId);
    }

    @Override
    public ResponseEntity<ReportReview> reportReviewReviewIdPost(Long reviewId, String body) {
        return reportReviewService.report(reviewId, body);
    }

    @Override
    public ResponseEntity<ReportComment> reportCommentCommentIdPost(Long commentId, String body) {
        return reportCommentService.report(commentId, body);
    }

    @Override
    public ResponseEntity<String> reportCommentDeleteReportIdUserIdDelete(Long userId, Long reportId) {
        return reportCommentService.delete(userId, reportId);
    }

}


