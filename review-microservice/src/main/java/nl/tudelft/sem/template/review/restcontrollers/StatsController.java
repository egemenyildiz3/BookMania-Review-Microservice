package nl.tudelft.sem.template.review.restcontrollers;

import nl.tudelft.sem.template.api.StatsApi;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.StatsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController implements StatsApi {

    private final StatsServiceImpl service;

    public StatsController(BookDataRepository repo, ReviewRepository reviewRepository, StatsServiceImpl service) {
        this.service = (service == null) ? new StatsServiceImpl(repo, reviewRepository) : service;
    }

    @Override
    public ResponseEntity<Double> statsAvgRatingBookIdGet(Long bookId) {
        return service.avgRating(bookId);
    }

    @Override
    public ResponseEntity<Long> statsInteractionsBookIdGet(Long bookId) {
        return service.interactions(bookId);
    }
}
