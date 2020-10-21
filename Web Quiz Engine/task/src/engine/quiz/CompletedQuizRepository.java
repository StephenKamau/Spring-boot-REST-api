package engine.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedQuizRepository extends JpaRepository<QuizCompletionData, Long> {
    @Query(
            value = "SELECT * FROM QUIZ_COMPLETION_DATA u WHERE u.EMAIL = :email ORDER BY completed_At DESC",
            nativeQuery = true)
    Page<QuizCompletionData> findAllCompletedBy(@Param(value = "email") String email, Pageable pageable);
}
