package nl.tudelft.sem.template.review.restcontrollers;

import nl.tudelft.sem.template.api.ReportApi;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController implements ReportApi {
    private final ReportReviewServiceImpl reportReviewService;
    private final ReportCommentServiceImpl reportCommentService;

    /**
     * Initializes a new instance of the reportReviewService and reportCommentService with the provided dependencies.
     *
     * @param reportReviewRepository report review repo
     * @param reviewRepository review repo
     * @param communicationService communication service
     * @param reportCommentRepository report comment repo
     * @param commentRepository comment repo
     * @param reportCommentService report comment service
     * @param reportReviewService Report review service
     */
    public ReportController(ReportReviewRepository reportReviewRepository,
                            ReviewRepository reviewRepository,
                            CommunicationServiceImpl communicationService,
                            ReportCommentRepository reportCommentRepository,
                            CommentRepository commentRepository,
                            ReportCommentServiceImpl reportCommentService,
                            ReportReviewServiceImpl reportReviewService) {
        this.reportReviewService = reportReviewService != null ? reportReviewService : new ReportReviewServiceImpl(
                reportReviewRepository,
                communicationService,
                reviewRepository);
        this.reportCommentService = reportCommentService != null ? reportCommentService : new ReportCommentServiceImpl(
                reportCommentRepository,
                communicationService,
                commentRepository);
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


