package Application.DataAccessLayer.Repositories;

import Application.Entities.questions.Question;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

@Repository
public interface QuestionRepository  extends JpaRepository<Question, UUID> {
    @Query(nativeQuery = true,
            value = "SELECT * FROM questions q " +
                    "LEFT JOIN question_tags t ON t.question_id = q.id " +
                    "WHERE (q.shared = true OR CAST(q.creator_id AS TEXT) = :user_id ) AND " +
                    "(:content IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :content , '%'))) AND " +
                    "(:difficulty IS NULL OR q.difficulty = :difficulty ) AND " +
                    "(:tags IS NULL OR t.tag IN :tags );")
    Page<Question> findByFilters(@Param("content") String content,
                                 @Param("difficulty") Integer difficulty,
                                 @Param("tags") List<String> tags,
                                 @Param("user_id") String userId,
                                 Pageable pageable);
}