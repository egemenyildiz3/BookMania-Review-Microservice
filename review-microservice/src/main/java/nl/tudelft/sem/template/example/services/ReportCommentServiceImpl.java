package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.example.repositories.ReportCommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportCommentServiceImpl implements ReportCommentService{
    private final ReportCommentRepository repo;
    private final CommentRepository commentRepo;

    public ReportCommentServiceImpl(ReportCommentRepository repo, CommentRepository commentRepo) {
        this.repo = repo;
        this.commentRepo = commentRepo;
    }

    @Override
    public ResponseEntity<ReportComment> report(Long commentId, String reason) {
        if ( reason == null || !commentRepo.existsById(commentId)) {
            return ResponseEntity.badRequest().header("bad").build();
        }
        ReportComment reportComment = new ReportComment();
        Comment com = commentRepo.getOne(commentId);
        reportComment.setCommentId(com.getId());
        reportComment.setReason(reason);
        com.addReportListItem(reportComment);
        commentRepo.save(com);

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
        Comment reported = commentRepo.getOne(reportComment.getCommentId());
        boolean isAdmin = isAdmin(userId);
        if(isAdmin){
            reported.getReportList().remove(reportComment);
            commentRepo.save(reported);
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
            repo.deleteAll(repo.findAllByCommentId(commentId));
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
