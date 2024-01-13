package nl.tudelft.sem.template.review.domain.review.filter;

import java.util.List;
import nl.tudelft.sem.template.model.Review;

public interface ReviewFilter {
    List<Review> filter(List<Review> reviews);
}
