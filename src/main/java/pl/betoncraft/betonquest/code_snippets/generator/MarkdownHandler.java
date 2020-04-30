package pl.betoncraft.betonquest.code_snippets.generator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownHandler {

    private static final Pattern EMPHASIS_REGEX = Pattern.compile("(([*_]){1,2})(?<keep>.*)\\1");
    private static final Pattern LINK_REGEX = Pattern.compile("!?\\[(?<keep>.+)\\]\\([^\\)\n]+\\)");
    private static final Pattern STROKE_REGEX = Pattern.compile("~~(?<keep>.*)~~");

    private String content;

    public MarkdownHandler(String content) {
        this.content = content;
    }

    private void strip(final Pattern regex) {
        Matcher m = regex.matcher(content);
        while (m.find()) {
            content = content.substring(0, m.start()) + m.group("keep") + content.substring(m.end());
            m = EMPHASIS_REGEX.matcher(content);
        }
    }

    public MarkdownHandler stripEmphasis() {
        strip(EMPHASIS_REGEX);
        return this;
    }

    public MarkdownHandler stripLinks() {
        strip(LINK_REGEX);
        return this;
    }

    public MarkdownHandler stripStrokes() {
        strip(STROKE_REGEX);
        return this;
    }


    @Override
    public String toString() {
        return content;
    }
}
