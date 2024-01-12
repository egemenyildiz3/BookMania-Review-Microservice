package nl.tudelft.sem.template.review.domain.review.filter;

import nl.tudelft.sem.template.model.Review;

import java.util.List;

public interface ReviewFilter {
    List<Review> filter(List<Review> reviews);
}
