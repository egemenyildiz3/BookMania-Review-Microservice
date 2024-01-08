package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    @Query("SELECT rc FROM ReportComment rc WHERE rc.comment.id = :commentId")
    List<ReportComment> findAllByCommentId(Long commentId);

    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN true ELSE false END FROM ReportComment rc WHERE rc.comment.id = :commentId")
    boolean existsByCommentId(Long commentId);
}
