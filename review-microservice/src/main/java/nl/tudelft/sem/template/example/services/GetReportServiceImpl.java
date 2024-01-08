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
    private final CommunicationServiceImpl communicationService;

    public GetReportServiceImpl(BookDataRepository bdr, ReviewRepository rr, CommunicationServiceImpl cs){
        this.bookDataRepository = bdr;
        this.reviewRepository = rr;
        this.communicationService = cs;
    }

    @Override
    public ResponseEntity<BookData> getReport(Long bookId, Long userId, String info) {
        if(userId == null || bookId == null || info == null) {
            return ResponseEntity.badRequest().build();
        }

        if(!communicationService.existsUser(userId)){
                return ResponseEntity.status(401)
                        .header("Error", "cannot find user.")
                        .build();
        }

        if(!communicationService.existsBook(bookId)){
            return ResponseEntity.status(400)
                    .header("Error", "cannot find book.")
                    .build();
        }

        // If the bookData doesn't exist yet, then create an empty one for the repository, and work with it later
        if(!bookDataRepository.existsById(bookId)){
            return createBookDataInRepository(bookId);
        }

        BookData result = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));

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
            return ResponseEntity.ok((result));
        }

        return ResponseEntity.status(400)
                .header("Error", "invalid info type.")
                .build();
    }

    @Override
    public ResponseEntity<BookData> addRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion) {
        if(!bookDataRepository.existsById(bookId)){
            var response = createBookDataInRepository(bookId);
            if(response.getStatusCode().is4xxClientError())
                return response;
        }

        BookData bd = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));

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

    @Override
    public ResponseEntity<BookData> removeRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion) {
        // If you're trying to delete a review, and there is no bookData object yet
        // then something has gone horribly wrong
        if(!bookDataRepository.existsById(bookId)){
            return ResponseEntity.badRequest().build();
        }

        BookData bd = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));

        int totalReviews = bd.getNegativeRev() + bd.getPositiveRev() + bd.getNegativeRev();
        double totalRating = bd.getAvrRating() * totalReviews;
        totalRating -= rating;
        totalReviews--;
        bd.setAvrRating(totalRating / totalReviews);


        switch (notion){
            case NEUTRAL -> bd.setNeutralRev(bd.getNeutralRev()-1);
            case NEGATIVE -> bd.setNegativeRev(bd.getNegativeRev()-1);
            case POSITIVE -> bd.setPositiveRev(bd.getPositiveRev()-1);
        }

        BookData saved = bookDataRepository.save(bd);

        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<BookData> updateRatingAndNotion(Long bookId, Long oldRating, Review.BookNotionEnum oldNotion,
                                                          Long newRating, Review.BookNotionEnum newNotion) {
        var response =  removeRatingAndNotion(bookId, oldRating, oldNotion);
        if(response.getStatusCode().is4xxClientError())
            return response;
        return addRatingAndNotion(bookId, newRating, newNotion);
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

    private BookData initializeLazyObjectFromDatabase(BookData bd){
        BookData result = new BookData(bd.getBookId());
        result.setPositiveRev(bd.getPositiveRev());
        result.setNegativeRev(bd.getNegativeRev());
        result.setAvrRating(bd.getAvrRating());
        result.setPositiveRev(bd.getPositiveRev());
        result.setNeutralRev(bd.getNeutralRev());
        // No need to set the most upvoted comment/review since they are null from the database
        return result;
    }
}
