package nl.tudelft.sem.template.review.domain.review.filter;

import nl.tudelft.sem.template.model.Review;

import java.util.Comparator;
import java.util.List;

public class MostRelevantFilter implements ReviewFilter{

    public List<Review> filter(List<Review> reviews) {
        reviews.sort(Comparator.comparingLong(
                review -> review.getDownvote() - review.getUpvote()
        ));
        return reviews;
    }
}
