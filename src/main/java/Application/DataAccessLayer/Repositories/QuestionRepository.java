package Application.DataAccessLayer.Repositories;

import Application.Entities.questions.Question;
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
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE " +
            "(:content IS NULL OR q.question LIKE %:content%) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "(:tags IS NULL OR t IN :tags)")
    Page<Question> findByFilters(@Param("content") String content,
                                 @Param("difficulty") Integer difficulty,
                                 @Param("tags") List<String> tags,
                                 Pageable pageable);
}