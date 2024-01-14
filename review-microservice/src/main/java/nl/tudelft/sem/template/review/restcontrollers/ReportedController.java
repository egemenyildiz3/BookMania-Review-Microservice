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

    public ReportedController(ReportReviewRepository reportReviewRepository, ReviewRepository reviewRepository,
                              CommunicationServiceImpl communicationService, ReportCommentRepository reportCommentRepository, CommentRepository commentRepository) {
        this.reportReviewService = new ReportReviewServiceImpl(reportReviewRepository, communicationService, reviewRepository);
        this.reportCommentService = new ReportCommentServiceImpl(reportCommentRepository, communicationService, commentRepository);
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
