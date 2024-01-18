package nl.tudelft.sem.template.review.domain.textcheck;

import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import java.util.Arrays;
import java.util.List;

public class ProfanityHandler extends BaseTextHandler {
    @Override
    public boolean handle(String text) {
        List<String> profanities = Arrays.asList("fuck", "shit", "motherfucker", "bastard", "cunt", "bitch");
        for (String p : profanities) {
            if (text.toLowerCase().contains(p)) {
                throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");
            }
        }
        return super.checkNext(text);
    }

}
