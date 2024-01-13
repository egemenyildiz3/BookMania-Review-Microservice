package nl.tudelft.sem.template.review.domain.review.filter;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.template.model.Review;

public class HighestRatedFilter implements ReviewFilter {

    public List<Review> filter(List<Review> reviews) {
        reviews.sort(Comparator.comparing(Review::getUpvote).reversed());
        return reviews;
    }
}
