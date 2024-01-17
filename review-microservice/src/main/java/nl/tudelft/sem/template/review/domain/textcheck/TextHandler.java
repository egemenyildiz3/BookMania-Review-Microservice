package nl.tudelft.sem.template.review.domain.textcheck;

public interface TextHandler {
    void setNext(TextHandler nextHandler);

    boolean handle(String text);
}
