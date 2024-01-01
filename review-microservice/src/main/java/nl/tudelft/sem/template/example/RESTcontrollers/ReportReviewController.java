package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReportApi;
import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.example.services.ReportReviewServiceImpl;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportReviewController implements ReportApi {
    private final ReportReviewServiceImpl service;

    public ReportReviewController(ReportReviewRepository repo) {
        this.service = new ReportReviewServiceImpl(repo);
    }

    @Override
    public ResponseEntity<String> reportReviewDeleteReportIdUserIdDelete(Long userId, Long id) {
        return service.delete(id, userId);
    }

    @Override
    public ResponseEntity<ReportReview> reportReviewPost(Review review) {
        return service.report(review);
    }
}
