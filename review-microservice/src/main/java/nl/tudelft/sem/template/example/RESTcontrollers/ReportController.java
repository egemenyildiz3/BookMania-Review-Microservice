package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReportApi;
import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.example.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.example.services.ReportReviewServiceImpl;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController implements ReportApi {
    private final ReportReviewServiceImpl reportReviewService;
    private final ReportCommentServiceImpl reportCommentService;

    public ReportController(ReportReviewRepository reportReviewRepository, ReviewRepository reviewRepo,
                            ReportCommentRepository reportCommentRepository, CommentRepository commentRepository) {
        this.reportReviewService = new ReportReviewServiceImpl(reportReviewRepository, reviewRepo);
        this.reportCommentService = new ReportCommentServiceImpl(reportCommentRepository,commentRepository);
    }

    @Override
    public ResponseEntity<String> reportReviewDeleteReportIdUserIdDelete(Long userId, Long id) {
        return reportReviewService.delete(id, userId);
    }

    @Override
    public ResponseEntity<ReportReview> reportReviewPost(Review review) {

        return reportReviewService.report(review);
    }

    @Override
    public ResponseEntity<String> reportCommentDeleteReportIdUserIdDelete(Long userId, Long id) {
        return reportCommentService.delete(id, userId);
    }

    @Override
    public ResponseEntity<ReportComment> reportCommentPost(Comment comment) {
        return reportCommentService.report(comment);
    }
}

