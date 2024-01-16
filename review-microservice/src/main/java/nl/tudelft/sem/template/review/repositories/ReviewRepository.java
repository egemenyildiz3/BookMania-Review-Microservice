package nl.tudelft.sem.template.review.repositories;

import java.util.List;
import nl.tudelft.sem.template.model.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Query used by getReport, to find the most upvoted review for a bookData report
    // Although I'm not sure if I should be accessing the repo myself or rather have a reference to the Reviewservice...
    @Query("SELECT r.id FROM Review r WHERE r.bookId = :bookId ORDER BY r.upvote DESC, r.id ASC")
    List<Long> findMostUpvotedReviewId(@Param("bookId") Long bookId, Pageable pageable);

    // Uses the naming convention of Spring Data JPA to automatically generate the appropriate query
    Long countByBookId(Long bookId);
}
