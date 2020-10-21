package engine.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
public class QuizCompletionData {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long stampId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;

    private long id;
    private Date completedAt;

    public QuizCompletionData() {
        super();
    }

    public QuizCompletionData(long quizId,String email, Date completedAt) {
        this.id = quizId;
        this.email = email;
        this.completedAt = completedAt;
    }

    @Id
    @Column(name = "stampId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getStampId() {
        return stampId;
    }

    public void setStampId(long stampId) {
        this.stampId = stampId;
    }

    @Column(name = "completedAt")
    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long quizId) {
        this.id = quizId;
    }
}
