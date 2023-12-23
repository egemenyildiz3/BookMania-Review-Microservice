package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.BookDataRepository;
import nl.tudelft.sem.template.model.BookData;
import org.springframework.http.ResponseEntity;

public class GetReportServiceImpl implements GetReportService{

    private final BookDataRepository bookDataRepository;

    public GetReportServiceImpl(BookDataRepository bdr){
        this.bookDataRepository = bdr;
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
            return ResponseEntity.ok(result);
        }
        if(info.equals("rating")){
            BookData rating = new BookData(bookId, result.getBookId());
            rating.setAvrRating(result.getAvrRating());
            return ResponseEntity.ok(rating);
        }
        if(info.equals("interactions")){
            BookData interactions = new BookData(bookId, result.getBookId());
            interactions.setNegativeRev(result.getNegativeRev());
            interactions.setNeutralRev(result.getNeutralRev());
            interactions.setPositiveRev(result.getPositiveRev());
            return ResponseEntity.ok(interactions);
        }

        // TODO: This means that the info type was incorrect. How do I indicate this though?
        // We would need a new response for the api
        return ResponseEntity.status(400).build();
    }
}
