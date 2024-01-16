package nl.tudelft.sem.template.review.domain.reviewsort;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.template.model.Review;

public class MostRelevantSort implements ReviewSort {

    public List<Review> sort(List<Review> reviews) {
        reviews.sort(Comparator.comparingLong(review -> review.getDownvote() - review.getUpvote()));
        return reviews;
    }
}
