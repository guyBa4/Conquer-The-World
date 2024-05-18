package Application.Repositories;

import Application.Entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository  extends JpaRepository<Question, UUID> {
    @Query("SELECT q FROM Question q WHERE " +
            "(:content IS NULL OR q.question LIKE %:content%) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty)")// AND " +
//            "(:tags IS NULL OR q.tags IN :tags)")
    Page<Question> findByFilters(@Param("content") String content,
                                 @Param("difficulty") int difficulty,
//                                 @Param("tags") List<String> tags,
                                 Pageable pageable);
}
