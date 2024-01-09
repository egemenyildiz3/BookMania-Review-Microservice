package nl.tudelft.sem.template.example.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface StatsService {

    ResponseEntity<Double> avgRating(Long bookId, Long userId);

    ResponseEntity<Long> interactions(Long bookId, Long userId);

}
