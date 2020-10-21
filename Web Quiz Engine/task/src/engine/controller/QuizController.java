package engine.controller;

import engine.quiz.*;
import engine.user.UserData;
import engine.user.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class QuizController {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    QuizService quizService;

    @Autowired
    CompletedQuizRepository completedQuizRepository;

    @Autowired
    CompletedQuizService completedQuizService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    private ResponseStatus success = new ResponseStatus(true, "Congratulations, you're right!");
    private ResponseStatus fail = new ResponseStatus(false, "Wrong answer! Please, try again.");

    private Quiz tempQuiz = new Quiz();

    public QuizController() {
    }

    //verify emails
    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    //create quiz
    @PostMapping(path = "/api/quizzes")
    public Quiz CreateQuiz(@RequestBody Quiz quiz, @RequestHeader String authorization) {
        tempQuiz = quiz;
        tempQuiz.setEmail(header2email(authorization));
        if (tempQuiz.getOptions() == null || tempQuiz.getOptions().length < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Minimum of two options required!");
        }
        return quizRepository.save(quiz);
    }

    //get quiz by id
    @GetMapping(path = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).isPresent() ? quizRepository.findById(id).get() : null;
        if (quiz != null) {
            return quiz;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Quiz not found!");
    }

    //get all quizzes
    @GetMapping(path = "/api/quizzes")
    public ResponseEntity<Page<Quiz>> getAllQuizzes(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Quiz> quizzes = quizService.getAllQuizzes(page, size);
        return new ResponseEntity<>(quizzes, new HttpHeaders(), HttpStatus.OK);
    }

    //get completed quizzes for current user
    @GetMapping(path = "/api/quizzes/completed")
    public ResponseEntity<Page<QuizCompletionData>> getAllQuizzes(@RequestHeader String authorization, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<QuizCompletionData> quizzes = completedQuizService.getAllQuizzes(page, size, header2email(authorization));
        return new ResponseEntity<>(quizzes, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    public ResponseStatus solveQuiz(@RequestHeader String authorization, @PathVariable Long id, @RequestBody Choice answer) {
        int[] emptyArray = new int[0];
        Quiz quiz = quizRepository.findById(id).isPresent() ? quizRepository.findById(id).get() : null;
        if (quiz != null) {
            long quizId = quiz.getId();
            String email = header2email(authorization);
            if (Arrays.equals(quiz.getAnswer(), answer.getAnswer())) {
                quizSolveSuccess(quizId, email);
                return success;
            } else if (quiz.getAnswer() == null && Arrays.equals(answer.getAnswer(), emptyArray)) {
                quizSolveSuccess(quizId, email);
                return success;
            } else if (answer.getAnswer() == null && Arrays.equals(quiz.getAnswer(), emptyArray)) {
                quizSolveSuccess(quizId, email);
                return success;
            } else if (!Arrays.equals(quiz.getAnswer(), answer.getAnswer())) {
                return fail;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Quiz not found!");
    }

    //User registration
    @PostMapping(path = "/api/register")
    public ResponseStatus register(@RequestBody UserData userData) {
        if (userData.getPassword().length() >= 5
                && userDetailRepository.findByEmail(userData.getEmail()).isEmpty()
                && validate(userData.getEmail())
        ) {
            String password = new BCryptPasswordEncoder().encode(userData.getPassword());
            userData.setPassword(password);
            userDetailRepository.save(userData);
            return new ResponseStatus(true, "Success");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "User exists");
    }

    //delete quizzes
    @DeleteMapping(path = "/api/quizzes/{id}")
    public ResponseEntity<String> deleteQuiz(@RequestHeader String Authorization, @PathVariable Long id) {
        Quiz quiz = quizRepository.findById(id).isPresent() ? quizRepository.findById(id).get() : null;
        if (quiz == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!quiz.getEmail().equals(header2email(Authorization)))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        quizRepository.delete(quiz);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/actuator/shutdown")
    public void voidMethod() {
        //test
    }

    //create quiz completion data
    public void quizSolveSuccess(long quizId, String email) {
        Date date = new Date();
        QuizCompletionData quizCompletionData = new QuizCompletionData(quizId, email, date);
        completedQuizRepository.save(quizCompletionData);
    }

    //decode email
    private String header2email(String header) {
        byte[] decodedBytes = Base64.getDecoder().decode(header.substring(6));
        String decodedString = new String(decodedBytes);
        String[] headers = decodedString.split(":");
        return headers[0];
    }
}
