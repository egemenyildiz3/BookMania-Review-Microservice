package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.ReportReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportReviewRepository extends JpaRepository<ReportReview, Long> {
    @Query("SELECT rr FROM ReportReview rr WHERE rr.reviewId = :reviewId")
    List<ReportReview> findAllByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT CASE WHEN COUNT(rr) > 0 THEN true ELSE false END FROM ReportReview rr WHERE rr.reviewId = :reviewId")
    boolean existsByReviewId(@Param("reviewId") Long reviewId);
}
