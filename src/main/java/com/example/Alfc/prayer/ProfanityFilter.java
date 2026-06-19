package com.example.Alfc.prayer;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Minimal client-language profanity / slur screen for prayer-wall submissions.
 * Deliberately small and conservative: false positives on a prayer wall are
 * worse than false negatives, since flagged-but-real posts get rejected for
 * the submitter and there's no human in the publish loop. Expand cautiously.
 */
@Component
public class ProfanityFilter {

    private static final Set<String> BLOCKED = Set.of(
            "fuck", "shit", "bitch", "asshole", "cunt",
            "nigger", "nigga", "faggot", "retard"
    );

    private static final Pattern WORD = Pattern.compile("[\\p{L}']+");

    public boolean isClean(String text) {
        if (text == null || text.isBlank()) return true;
        var m = WORD.matcher(text.toLowerCase());
        while (m.find()) {
            if (BLOCKED.contains(m.group())) return false;
        }
        return true;
    }
}
