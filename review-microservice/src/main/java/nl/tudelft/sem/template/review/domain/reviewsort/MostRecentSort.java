package nl.tudelft.sem.template.review.domain.reviewsort;

import java.util.Comparator;
import java.util.List;
import nl.tudelft.sem.template.model.Review;

public class MostRecentSort implements ReviewSort {
    public List<Review> sort(List<Review> reviews) {
        reviews.sort(Comparator.comparing(Review::getTimeCreated).reversed());
        return reviews;
    }
}
