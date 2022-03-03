package Objects;

public class Question {
    private int id;
    private String content;
    private String answer;

    public Question(int id, String content, String answer){
        this.setId(id);
        this.setContent(content);
        this.setAnswer(answer);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
