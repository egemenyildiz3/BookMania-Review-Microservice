package nl.tudelft.sem.template.review.domain.textcheck;

import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;

public class UrlHandler extends BaseTextHandler {

    @Override
    public boolean handle(String text) {
        if (text.matches(".*https?://.*")) {
            throw new CustomBadRequestException("Text shouldn't contain URLs.");
        }
        return super.checkNext(text);
    }

}
