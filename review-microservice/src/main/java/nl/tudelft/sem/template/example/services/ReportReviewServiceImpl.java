package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportReviewServiceImpl implements ReportReviewService{

    private final ReportReviewRepository repo;
    private final ReviewRepository reviewRepo;

    public ReportReviewServiceImpl(ReportReviewRepository repo, ReviewRepository reviewRepo) {
        this.repo = repo;
        this.reviewRepo = reviewRepo;
    }

    @Override
    public ResponseEntity<ReportReview> report(Review review) {
        if (review == null || !reviewRepo.existsById(review.getId())) {
            return ResponseEntity.badRequest().build();
        }

        ReportReview reportReview = new ReportReview();
        reportReview.setReview(review);

        repo.save(reportReview);

        return ResponseEntity.ok(reportReview);
    }

    @Override
    public ResponseEntity<ReportReview> get(Long id) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @Override
    public ResponseEntity<List<ReportReview>> getReportsForReview(Long reviewId) {
        List<ReportReview> reports = repo.findAllByReviewId(reviewId);
        return ResponseEntity.ok(reports);
    }

    @Override
    public ResponseEntity<List<ReportReview>> getAllReportedReviews(Long userId) {
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            List<ReportReview> allReportedReviews = repo.findAll();
            return ResponseEntity.ok(allReportedReviews);
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Boolean> isReported(Long reviewId) {
        boolean isReported = repo.existsByReviewId(reviewId);
        return ResponseEntity.ok(isReported);
    }

    @Override
    public ResponseEntity<String> delete(Long id, Long userId) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        ReportReview reportReview = repo.findById(id).get();
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<String> deleteReportsForReview(Long reviewId, Long userId) {
        if(!repo.existsByReviewId(reviewId)) {
            return ResponseEntity.badRequest().build();
        }
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            for (ReportReview report : repo.findAllByReviewId(reviewId)) {
                repo.delete(report);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public static boolean isAdmin(Long userId){
        //TODO: call the method from user microservice that returns the role of user
        // return true if admin
        return true;
    }
}
