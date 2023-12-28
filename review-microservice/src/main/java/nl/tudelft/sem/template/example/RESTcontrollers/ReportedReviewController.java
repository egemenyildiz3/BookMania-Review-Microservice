package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReportedApi;
import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.example.services.ReportReviewServiceImpl;
import nl.tudelft.sem.template.model.ReportReview;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportedReviewController implements ReportedApi{
    private final ReportReviewServiceImpl service;

    public ReportedReviewController(ReportReviewRepository repo) {
        this.service = new ReportReviewServiceImpl(repo);
    }

    @Override
    public ResponseEntity<List<ReportReview>> reportedReviewsUserIdGet(Long userId) {
        return service.getAllReportedReviews(userId);
    }
}
