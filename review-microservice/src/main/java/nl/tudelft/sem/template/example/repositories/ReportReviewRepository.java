package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
    List<ReportReview> findAllByReviewId(Long reviewId);
    boolean existsByReviewId(Long reviewId);
}
