package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.GetReportApi;
import nl.tudelft.sem.template.example.repositories.BookDataRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.example.services.GetReportService;
import nl.tudelft.sem.template.example.services.GetReportServiceImpl;
import nl.tudelft.sem.template.example.services.ReviewServiceImpl;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.awt.print.Book;
import java.util.Optional;

@RestController
public class GetReportController implements GetReportApi {

    private final GetReportServiceImpl service;

    public GetReportController(BookDataRepository repo, ReviewRepository rr, CommunicationServiceImpl cs) {
        this.service = new GetReportServiceImpl(repo, rr, cs);
    }


    @Override
    public ResponseEntity<BookData> getReportBookIdUserIdInfoGet(Long bookId, Long userId, String info) {
        return service.getReport(bookId, userId, info);
    }
}
