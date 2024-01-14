package nl.tudelft.sem.template.review.restcontrollers;

import nl.tudelft.sem.template.api.GetReportApi;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.CommentService;
import nl.tudelft.sem.template.review.services.CommentServiceImpl;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.GetReportServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetReportController implements GetReportApi {

    private final GetReportServiceImpl service;

    private final CommunicationServiceImpl cs;

    private final BookDataRepository bookDataRepository;

    public GetReportController(BookDataRepository repo,
                               ReviewRepository rr,
                               CommunicationServiceImpl cs,
                               CommentRepository cr,
                               GetReportServiceImpl service
    )
    {
        this.cs = cs;
        bookDataRepository = repo;
        CommentService co = new CommentServiceImpl(cr, rr);
        this.service = service != null ? service : new GetReportServiceImpl(repo, rr, cs, co);
    }


    @Override
    public ResponseEntity<BookData> getReportBookIdUserIdInfoGet(Long bookId, Long userId, String info) {
        if (userId == null) {
            throw new CustomBadRequestException("UserId cannot be null");
        }
        if (bookId == null) {
            throw new CustomBadRequestException("bookId cannot be null");
        }
        if (info == null) {
            throw new CustomBadRequestException("info cannot be null");
        }
        if (!cs.existsUser(userId)) {
            throw new CustomUserExistsException("User doesn't exist");
        }

        if (!cs.existsBook(bookId)) {
            throw new CustomBadRequestException("Book doesn't exist");
        }

        // If the bookData doesn't exist yet, then create an empty one for the repository, and work with it later
        if (!bookDataRepository.existsById(bookId)) {
            return service.createBookDataInRepository(bookId);
        }

        return service.getReport(bookId, userId, info);
    }
}
