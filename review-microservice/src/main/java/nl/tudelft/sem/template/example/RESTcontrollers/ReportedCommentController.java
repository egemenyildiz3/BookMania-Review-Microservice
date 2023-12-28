package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReportedApi;
import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.example.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.model.ReportComment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportedCommentController implements ReportedApi {
    private final ReportCommentServiceImpl service;

    public ReportedCommentController(ReportCommentRepository repo) {
        this.service = new ReportCommentServiceImpl(repo);
    }

    @Override
    public ResponseEntity<List<ReportComment>> reportedCommentsUserIdGet(Long userId) {
        return service.getAllReportedComments(userId);
    }
}
