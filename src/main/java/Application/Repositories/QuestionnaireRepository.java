package Application.Repositories;

import Application.Entities.questions.Questionnaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, UUID> {
    
    @Query("SELECT q FROM Questionnaire q WHERE q.name LIKE %:name% AND q.creator.id = :userId")
    Page<Questionnaire> findByNameLikeAndUserId(@Param("name") String name, @Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT q FROM Questionnaire q WHERE q.name LIKE %:name%")
    Page<Questionnaire> findByNameLike(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT q FROM Questionnaire q WHERE q.creator.id = :userId")
    Page<Questionnaire> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    @Query("SELECT q FROM Questionnaire q")
    Page<Questionnaire> findBy(Pageable pageable);
}
