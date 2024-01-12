package nl.tudelft.sem.template.review.domain.review.filter;

import nl.tudelft.sem.template.model.Review;

import java.util.Comparator;
import java.util.List;

public class HighestRatedFilter implements ReviewFilter{

    public List<Review> filter(List<Review> reviews) {
        reviews.sort(Comparator.comparing(Review::getUpvote).reversed());
        return reviews;
    }
}
