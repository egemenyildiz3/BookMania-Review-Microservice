package nl.tudelft.sem.template.review.services;

import org.springframework.http.ResponseEntity;

public interface StatsService {

    ResponseEntity<Double> avgRating(Long bookId);

    ResponseEntity<Long> interactions(Long bookId);

}
