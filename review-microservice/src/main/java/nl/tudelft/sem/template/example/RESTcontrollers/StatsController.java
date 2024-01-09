package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.StatsApi;
import nl.tudelft.sem.template.example.repositories.BookDataRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.services.StatsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController implements StatsApi {

    private final StatsServiceImpl service;

    public StatsController(BookDataRepository repo, ReviewRepository reviewRepository) {
        this.service = new StatsServiceImpl(repo, reviewRepository);
    }

    @Override
    public ResponseEntity<Double> statsAvgRatingBookIdUserIdGet(Long bookId, Long userId) {
        return service.avgRating(bookId, userId);
    }

    @Override
    public ResponseEntity<Long> statsInteractionsBookIdUserIdGet(Long bookId, Long userId) {
        return service.interactions(bookId, userId);
    }
}
