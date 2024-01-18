package nl.tudelft.sem.template.review.restcontrollers;

import java.util.List;
import nl.tudelft.sem.template.api.ReportedApi;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.review.services.ReportReviewServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportedController implements ReportedApi {
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
     * @param reportReviewService Report review service reportReviewService
     */
    public ReportedController(ReportReviewRepository reportReviewRepository,
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
    public ResponseEntity<List<ReportReview>> reportedReviewsUserIdGet(Long userId) {
        return reportReviewService.getAllReportedReviews(userId);
    }

    @Override
    public ResponseEntity<List<ReportComment>> reportedCommentsUserIdGet(Long userId) {
        return reportCommentService.getAllReportedComments(userId);
    }
}
