package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import nl.tudelft.sem.template.model.ReportReview;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportCommentServiceImpl implements ReportCommentService{
    private final ReportCommentRepository repo;

    public ReportCommentServiceImpl(ReportCommentRepository repo) {
        this.repo = repo;
    }
    @Override
    public ResponseEntity<ReportComment> report(Comment comment) {
        if (comment == null) {
            return ResponseEntity.badRequest().build();
        }

        ReportComment reportComment = new ReportComment();
        reportComment.setComment(comment);

        repo.save(reportComment);

        return ResponseEntity.ok(reportComment);
    }

    @Override
    public ResponseEntity<ReportComment> get(Long id) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @Override
    public ResponseEntity<List<ReportComment>> getReportsForComment(Long commentId) {
        List<ReportComment> reports = repo.findAllByCommentId(commentId);
        return ResponseEntity.ok(reports);
    }

    @Override
    public ResponseEntity<List<ReportComment>> getAllReportedComments(Long userId) {
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            List<ReportComment> allReportedComments = repo.findAll();
            return ResponseEntity.ok(allReportedComments);
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Boolean> isReported(Long commentId) {
        boolean isReported = repo.existsByCommentId(commentId);
        return ResponseEntity.ok(isReported);
    }

    @Override
    public ResponseEntity<String> delete(Long id, Long userId) {
        if(!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        ReportComment reportComment = repo.findById(id).get();
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<String> deleteReportsForComment(Long commentId, Long userId) {
        if(!repo.existsByCommentId(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            for (ReportComment report : repo.findAllByCommentId(commentId)) {
                repo.delete(report);
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public static boolean isAdmin(Long userId){
        //TODO: call the method from user microservice that returns the role of user
        // return true if admin
        return true;
    }
}
