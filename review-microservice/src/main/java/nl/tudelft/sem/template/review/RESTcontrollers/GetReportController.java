package nl.tudelft.sem.template.review.RESTcontrollers;

import nl.tudelft.sem.template.api.GetReportApi;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.*;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.GetReportServiceImpl;
import nl.tudelft.sem.template.model.BookData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetReportController implements GetReportApi {

    private final GetReportServiceImpl service;

    public GetReportController(BookDataRepository repo, ReviewRepository rr, CommunicationServiceImpl cs, CommentRepository cr) {
        CommentService co = new CommentServiceImpl(cr, rr);
        this.service = new GetReportServiceImpl(repo, rr, cs, co);
    }


    @Override
    public ResponseEntity<BookData> getReportBookIdUserIdInfoGet(Long bookId, Long userId, String info) {
        return service.getReport(bookId, userId, info);
    }
}
