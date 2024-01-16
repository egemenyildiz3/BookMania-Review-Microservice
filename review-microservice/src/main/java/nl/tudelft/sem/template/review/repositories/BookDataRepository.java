package nl.tudelft.sem.template.review.repositories;

import nl.tudelft.sem.template.model.BookData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDataRepository extends JpaRepository<BookData, Long> {
}
