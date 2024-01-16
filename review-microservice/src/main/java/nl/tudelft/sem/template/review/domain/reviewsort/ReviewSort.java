package nl.tudelft.sem.template.review.domain.reviewsort;

import java.util.List;
import nl.tudelft.sem.template.model.Review;

public interface ReviewSort {
    List<Review> sort(List<Review> reviews);
}
