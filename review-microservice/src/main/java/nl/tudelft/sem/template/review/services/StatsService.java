package nl.tudelft.sem.template.review.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface StatsService {

    ResponseEntity<Double> avgRating(Long bookId);

    ResponseEntity<Long> interactions(Long bookId);

}
