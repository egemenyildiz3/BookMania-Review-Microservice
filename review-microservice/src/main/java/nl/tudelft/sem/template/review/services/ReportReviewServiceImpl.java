package nl.tudelft.sem.template.review.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import org.springframework.stereotype.Service;

@Service
public class ReportReviewServiceImpl implements ReportReviewService {

    private final ReportReviewRepository repo;
    private final CommunicationServiceImpl communicationService;
    private final ReviewRepository reviewRepo;

    public ReportReviewServiceImpl(ReportReviewRepository repo, CommunicationServiceImpl communicationService,
                                   ReviewRepository reviewRepo) {
        this.repo = repo;
        this.communicationService =  communicationService;
        this.reviewRepo = reviewRepo;
    }

    @Override
    public ResponseEntity<ReportReview> report(Long reviewId, String reason) {
        if (reviewId == null) {
            throw new CustomBadRequestException("Review ID cannot be null.");
        }
        if (reason == null) {
            throw new CustomBadRequestException("Reason cannot be null.");
        }
        boolean existsReviewId = reviewRepo.existsById(reviewId);
        if (!existsReviewId) {
            throw new CustomBadRequestException("Invalid review id.");
        }

        Review review = reviewRepo.getOne(reviewId);

        boolean existsUser = communicationService.existsUser(review.getUserId());
        if (!existsUser) {
            throw new CustomBadRequestException("Invalid user id.");
        }

        if (ReviewServiceImpl.checkProfanities(review.getText())) {
            throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");
        }

        ReportReview reportReview = new ReportReview();
        Review rev = reviewRepo.getOne(reviewId);
        rev.addReportListItem(reportReview);
        reportReview.setReason(reason);
        reportReview.setReviewId(rev.getId());

        reviewRepo.save(rev);

        return ResponseEntity.ok(rev.getReportList().get(rev.getReportList().size() - 1));
    }

    @Override
    public ResponseEntity<ReportReview> get(Long id) {
        if (!repo.existsById(id)) {
            throw new CustomBadRequestException("Invalid report id.");
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @Override
    public ResponseEntity<List<ReportReview>> getReportsForReview(Long reviewId) {
        if (!reviewRepo.existsById(reviewId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        List<ReportReview> reports = repo.findAllByReviewId(reviewId);
        return ResponseEntity.ok(reports);
    }

    @Override
    public ResponseEntity<List<ReportReview>> getAllReportedReviews(Long userId) {

        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin) {
            throw new CustomPermissionsException("User is not owner or admin.");
        }
        else if (isAdmin) {
            List<ReportReview> allReportedReviews = repo.findAll();
            return ResponseEntity.ok(allReportedReviews);
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Boolean> isReported(Long reviewId) {
        boolean existsReviewId = reviewRepo.existsById(reviewId);
        if (!existsReviewId) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        boolean isReported = repo.existsByReviewId(reviewId);
        return ResponseEntity.ok(isReported);
    }

    @Override
    public ResponseEntity<String> delete(Long id, Long userId) {
        if (!repo.existsById(id)) {
            throw new CustomBadRequestException("Invalid report id.");
        }

        Optional<ReportReview> optionalReportReview = repo.findById(id);
        ReportReview reportReview;
        if (optionalReportReview.isPresent()) {
            reportReview = optionalReportReview.get();
        } else {
            throw new CustomBadRequestException("Cannot find report.");
        }

        boolean isAdmin = communicationService.isAdmin(userId);
        if (isAdmin) {
            Review review = reviewRepo.getOne(reportReview.getReviewId());
            review.getReportList().remove(reportReview);
            reviewRepo.save(review);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().header("not admin").build();

    }

    @Override
    public ResponseEntity<String> deleteReportsForReview(Long reviewId, Long userId) {
        if (!reviewRepo.existsById(reviewId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin) {
            throw new CustomPermissionsException("User is not owner or admin.");
        }
        else if (isAdmin) {
            List<ReportReview> allReportedReviews = repo.findAllByReviewId(reviewId);
            for (ReportReview reportReview : allReportedReviews) {
                Review review = reviewRepo.getOne(reportReview.getReviewId());
                review.getReportList().remove(reportReview);
                reviewRepo.save(review);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
