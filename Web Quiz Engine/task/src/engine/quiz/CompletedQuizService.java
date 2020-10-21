package engine.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CompletedQuizService {
    @Autowired
    CompletedQuizRepository completedQuizRepository;

    public Page<QuizCompletionData> getAllQuizzes(int pageNumber, int pageSize, String email) {
        Pageable paging = PageRequest.of(pageNumber, pageSize);
        return completedQuizRepository.findAllCompletedBy(email, paging);
    }
}