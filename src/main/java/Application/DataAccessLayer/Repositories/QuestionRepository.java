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
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN q.tags t WHERE " +
            "q.shared = true AND " +
            "(:content IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :content, '%'))) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "(:tags IS NULL OR t IN :tags)")
    Page<Question> findByFilters(@Param("content") String content,
                                 @Param("difficulty") Integer difficulty,
                                 @Param("tags") List<String> tags,
                                 Pageable pageable);

//    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN q.tags t WHERE " +
//            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
//            "(:tags IS NULL OR t IN :tags) AND " +
//            "(:content IS NULL OR " +
//            " (COALESCE(:content) IS NOT NULL AND " +
//            "   ( " +
//            "     (LOWER(q.question) LIKE CONCAT('%', LOWER(:content[0]), '%')) " +
//            "     OR (LOWER(q.question) LIKE CONCAT('%', LOWER(:content[1]), '%')) " +
//            "     OR (LOWER(q.question) LIKE CONCAT('%', LOWER(:content[2]), '%')) " +
//            "     OR (LOWER(q.question) LIKE CONCAT('%', LOWER(:content[3]), '%')) " +
//            "     OR (LOWER(q.question) LIKE CONCAT('%', LOWER(:content[4]), '%')) " +
//            "   ) " +
//            "))")
//    Page<Question> findByFilters(@Param("content") List<String> content,
//                                 @Param("difficulty") Integer difficulty,
//                                 @Param("tags") List<String> tags,
//                                 Pageable pageable);

//    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN q.tags t WHERE " +
//            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
//            "(:tags IS NULL OR t IN :tags) AND " +
//            "(:content IS NULL OR " +
//            "  ( " +
//            "    (q.question LIKE %:#{#content[0]}%) " +
//            "    OR (q.question LIKE %:#{#content[1]}%) " +
//            "    OR (q.question LIKE %:#{#content[2]}%) " +
//            "    OR (q.question LIKE %:#{#content[3]}%) " +
//            "    OR (q.question LIKE %:#{#content[4]}%) " +
//            "  ) " +
//            "))")
//    Page<Question> findByFilters(@Param("content") String[] content,
//                                 @Param("difficulty") Integer difficulty,
//                                 @Param("tags") List<String> tags,
//                                 Pageable pageable);

}