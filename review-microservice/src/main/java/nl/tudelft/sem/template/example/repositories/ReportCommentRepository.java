package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    List<ReportComment> findAllByCommentId(Long commentId);
    boolean existsByCommentId(Long commentId);
}
