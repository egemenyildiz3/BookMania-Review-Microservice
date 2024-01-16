package nl.tudelft.sem.template.review.services;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.ReportComment;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReportCommentRepository;
import org.springframework.http.ResponseEntity;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import org.springframework.stereotype.Service;

@Service
public class ReportCommentServiceImpl implements ReportCommentService {
    private final ReportCommentRepository repo;
    private final CommunicationServiceImpl communicationService;
    private final CommentRepository commentRepo;

    /**
     * Constructor for the ReportCommentServiceImpl.
     *
     * @param repo - the reportCommentRepository
     * @param communicationService - the communicationService
     * @param commentRepo - the commentRepository
     */
    public ReportCommentServiceImpl(ReportCommentRepository repo, CommunicationServiceImpl communicationService,
                                    CommentRepository commentRepo) {
        this.repo = repo;
        this.communicationService =  communicationService;
        this.commentRepo = commentRepo;
    }

    @Override
    public ResponseEntity<ReportComment> report(Long commentId, String reason) {
        if (commentId == null) {
            throw new CustomBadRequestException("Comment ID cannot be null.");
        }
        if (reason == null) {
            throw new CustomBadRequestException("Reason cannot be null.");
        }
        boolean existsCommentId = commentRepo.existsById(commentId);
        if (!existsCommentId) {
            throw new CustomBadRequestException("Invalid comment id.");
        }

        Comment comment = commentRepo.getOne(commentId);

        boolean existsUser = communicationService.existsUser(comment.getUserId());
        if (!existsUser) {
            throw new CustomBadRequestException("Invalid user id.");
        }


        ReportComment reportComment = new ReportComment();
        Comment com = commentRepo.getOne(commentId);
        com.addReportListItem(reportComment);
        reportComment.setReason(reason);
        reportComment.setCommentId(com.getId());

        repo.save(reportComment);
        return ResponseEntity.ok(reportComment);
    }



    @Override
    public ResponseEntity<ReportComment> get(Long id) {
        if (!repo.existsById(id)) {
            throw new CustomBadRequestException("Report id does not exist.");
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @Override
    public ResponseEntity<List<ReportComment>> getReportsForComment(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new CustomBadRequestException("Comment id does not exist.");
        }
        List<ReportComment> allReportsForComment = repo.findAllByCommentId(commentId);
        return ResponseEntity.ok(allReportsForComment);
    }

    @Override
    public ResponseEntity<List<ReportComment>> getAllReportedComments(Long userId) {
        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin) {
            throw new CustomPermissionsException("User is not admin.");
        }

        List<ReportComment> allReportedComments = repo.findAll();
        return ResponseEntity.ok(allReportedComments);
    }

    @Override
    public ResponseEntity<Boolean> isReported(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new CustomBadRequestException("Comment id does not exist.");
        }
        boolean isReported = repo.existsByCommentId(commentId);
        return ResponseEntity.ok(isReported);
    }

    @Override
    public ResponseEntity<String> delete(Long id, Long userId) {
        if (!repo.existsById(id)) {
            throw new CustomBadRequestException("Invalid report id.");
        }
        Optional<ReportComment> optionalReportComment = repo.findById(id);
        ReportComment reportComment;
        if (optionalReportComment.isPresent()) {
            reportComment = optionalReportComment.get();
        } else {
            throw new CustomBadRequestException("Cannot find report.");
        }

        boolean isAdmin = communicationService.isAdmin(userId);
        if (isAdmin) {
            Comment comment = commentRepo.getOne(reportComment.getCommentId());
            comment.getReportList().remove(reportComment);
            commentRepo.save(comment);
            return ResponseEntity.ok().build();
        }
        throw new CustomPermissionsException("User is not admin.");
    }

    @Override
    public ResponseEntity<String> deleteReportsForComment(Long commentId, Long userId) {
        if (!commentRepo.existsById(commentId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin) {
            throw new CustomPermissionsException("User is not owner or admin.");
        }

        List<ReportComment> allReportedComments = repo.findAllByCommentId(commentId);
        for (ReportComment reportComment : allReportedComments) {
            Comment comment = commentRepo.getOne(reportComment.getCommentId());
            comment.getReportList().remove(reportComment);
            commentRepo.save(comment);
        }
        return ResponseEntity.ok("Deleted all reports");

    }
}
