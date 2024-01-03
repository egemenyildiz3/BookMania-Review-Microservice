package nl.tudelft.sem.template.example.domain.review.filter;

import nl.tudelft.sem.template.model.Review;

import java.util.Comparator;
import java.util.List;

public class MostRecentFilter implements ReviewFilter{
    public List<Review> filter(List<Review> reviews) {
        reviews.sort(Comparator.comparing(Review::getTimeCreated).reversed());
        return reviews;
    }
}
