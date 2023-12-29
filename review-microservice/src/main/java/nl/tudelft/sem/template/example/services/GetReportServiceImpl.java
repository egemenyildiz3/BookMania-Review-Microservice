package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.BookDataRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetReportServiceImpl implements GetReportService{

    private final BookDataRepository bookDataRepository;
    private final ReviewRepository reviewRepository;

    public GetReportServiceImpl(BookDataRepository bdr, ReviewRepository rr){
        this.bookDataRepository = bdr;
        this.reviewRepository = rr;
    }

    @Override
    public ResponseEntity<BookData> getReport(Long bookId, String userId, String info) {

        if(userId == null || bookId == null || info == null) {
            return ResponseEntity.badRequest().build();
        }

        // TODO: Check if user exists somehow
        if(false){
                return ResponseEntity.status(401).build();
        }
        if(!bookDataRepository.existsById(bookId)){
            return ResponseEntity.status(400).build();
        }
        BookData result = bookDataRepository.getOne(bookId);

        if(info.equals("report")){
            // TODO get all the comments and reviews of my boy and find the ones with the most upvoted ones
            List<Long> reviewIds = reviewRepository.findMostUpvotedReviewId(bookId, PageRequest.of(0, 1));

            if(!reviewIds.isEmpty()){
                result.setMostUpvotedReview(reviewIds.get(0));
            }
            return ResponseEntity.ok(result);
        }
        if(info.equals("rating")){
            BookData rating = new BookData(bookId);
            rating.setAvrRating(result.getAvrRating());
            return ResponseEntity.ok(rating);
        }
        if(info.equals("interactions")){
            /* Initially did this, and I guess this is exactly the same as returning the object from the db
            BookData interactions = new BookData(bookId);
            interactions.setAvrRating(result.getAvrRating());
            interactions.setNegativeRev(result.getNegativeRev());
            interactions.setNeutralRev(result.getNeutralRev());
            interactions.setPositiveRev(result.getPositiveRev());
            */

            return ResponseEntity.ok(result);
        }

        // TODO: This means that the info type was incorrect. How do I indicate this though?
        // We would need a new response for the api
        return ResponseEntity.status(400).build();
    }

    @Override
    public ResponseEntity<BookData> addRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion) {
        if(bookDataRepository.existsById(bookId)){
            return ResponseEntity.status(400).build();
        }

        BookData bd = bookDataRepository.getOne(bookId);
        bd.setBookId(bookId);

        int totalReviews = bd.getNegativeRev() + bd.getPositiveRev() + bd.getNegativeRev();
        double totalRating = bd.getAvrRating() * totalReviews;
        totalRating += rating;
        totalReviews++;
        bd.setAvrRating(totalRating / totalReviews);


        switch (notion){
            case NEUTRAL -> bd.setNeutralRev(bd.getNeutralRev()+1);
            case NEGATIVE -> bd.setNegativeRev(bd.getNegativeRev()+1);
            case POSITIVE -> bd.setPositiveRev(bd.getPositiveRev()+1);
        }

        BookData saved = bookDataRepository.save(bd);

        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<BookData> createBookDataInRepository(Long bookId) {
        if(bookDataRepository.existsById(bookId)){
            return ResponseEntity.status(400).build();
        }

        BookData bd = new BookData(bookId);
        bd.setAvrRating(0.0);
        bd.setPositiveRev(0);
        bd.setNeutralRev(0);
        bd.setNegativeRev(0);

        bd = bookDataRepository.save(bd);

        return ResponseEntity.ok(bd);
    }
}
