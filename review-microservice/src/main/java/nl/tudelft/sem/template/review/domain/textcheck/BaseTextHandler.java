package nl.tudelft.sem.template.review.domain.textcheck;

public abstract class BaseTextHandler implements TextHandler {
    private TextHandler textHandler;

    @Override
    public void setNext(TextHandler textHandler) {
        this.textHandler = textHandler;
    }

    protected boolean checkNext(String text) {
        if (textHandler == null) {
            return true;
        }
        return textHandler.handle(text);
    }
}
