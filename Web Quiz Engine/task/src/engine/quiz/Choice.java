package engine.quiz;

public class Choice {
    private int[] answer;

    public Choice() {
        super();
    }

    public Choice(int[] answer) {
        this.answer = answer;
    }

    public int[] getAnswer() {
        return answer;
    }

    public void setAnswer(int[] answer) {
        this.answer = answer;
    }
}
