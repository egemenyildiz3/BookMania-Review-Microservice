package nl.tudelft.sem.template.review.domain.reviewsort;

import nl.tudelft.sem.template.model.Review;
import java.util.List;

public class SortContext {
    private ReviewSort reviewSort;

    public void setSort(ReviewSort reviewSort) {
        this.reviewSort = reviewSort;
    }

    public List<Review> sort(List<Review> reviews) {
        return reviewSort.sort(reviews);
    }
}
