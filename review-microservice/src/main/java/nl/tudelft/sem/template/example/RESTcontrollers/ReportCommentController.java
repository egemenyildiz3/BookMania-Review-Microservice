package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReportApi;
import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.example.services.ReportCommentServiceImpl;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportCommentController implements ReportApi {
    private final ReportCommentServiceImpl service;

    public ReportCommentController(ReportCommentRepository repo) {
        this.service = new ReportCommentServiceImpl(repo);
    }

    @Override
    public ResponseEntity<String> reportCommentDeleteReportIdUserIdDelete(Long userId, Long id) {
        return service.delete(id, userId);
    }

    @Override
    public ResponseEntity<ReportComment> reportCommentPost(Comment comment, String reason) {
        return service.report(comment, reason);
    }
}
