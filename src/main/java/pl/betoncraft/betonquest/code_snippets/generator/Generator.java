package pl.betoncraft.betonquest.code_snippets.generator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Generator {

    private static final String
            EVENTS_URL = "https://raw.githubusercontent.com/SaltyAimbOtter/BetonQuest/master/documentation/User-Documentation/Events-List.md",
            CONDITIONS_URL = "https://raw.githubusercontent.com/SaltyAimbOtter/BetonQuest/master/documentation/User-Documentation/Conditions-List.md",
            OBJECTIVES_URL = "https://raw.githubusercontent.com/SaltyAimbOtter/BetonQuest/master/documentation/User-Documentation/Objectives-List.md";

    private static final Pattern EXTRACTION_PATTERN = Pattern.compile(
            "^## (?<label>.*):\\s+`(?<prefix>.*)`\\s(?<description>(\\n([^\\n#][^\\n]*)?)+)",
            Pattern.MULTILINE
    );
    private static final Pattern EXAMPLE_PATTERN = Pattern.compile(
            "^\\s*!!! example\\s*\\n\\s*```.*\\s*(?<example>(.|\\n)+?)\\s*```\\s*",
            Pattern.MULTILINE
    );

    private final List<String> added = new ArrayList<>();
    private final List<String> updated = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Generator g = new Generator();
        new File("./snippets").mkdirs();

        System.out.println("Processing events...");
        g.updateSnippets("Event", new File("./snippets/events.json"), EVENTS_URL);

        System.out.println("Processing conditions...");
        g.updateSnippets("Condition", new File("./snippets/conditions.json"), CONDITIONS_URL);

        System.out.println("Processing objectives...");
        g.updateSnippets("Objective", new File("./snippets/objectives.json"), OBJECTIVES_URL);

        g.printChangeLog();
    }

    public JSONObject updateSnippets(String category, JSONObject snippets, String sourceURL) throws IOException {
        final BufferedReader source = new BufferedReader(
                new InputStreamReader(new URL(sourceURL).openStream(), StandardCharsets.UTF_8)
        );
        final String lines = source.lines().collect(Collectors.joining("\n"));
        source.close();

        final Matcher matcher = EXTRACTION_PATTERN.matcher(lines);
        while (matcher.find()) {
            final String label = matcher.group("label") + " " + category;
            final String prefix = matcher.group("prefix");
            String description = matcher.group("description");
            String example = "";
            Matcher exampleMatcher = EXAMPLE_PATTERN.matcher(description);
            while (exampleMatcher.find()) {
                if (example.isEmpty()) {
                    example = exampleMatcher.group("example");
                }
                description = description.substring(0, exampleMatcher.start()) + description.substring(exampleMatcher.end());
                exampleMatcher = EXAMPLE_PATTERN.matcher(description);
            }
            description = new MarkdownHandler(description).stripEmphasis().stripLinks().stripStrokes().toString();

            if (snippets.has(label)) {
                final JSONObject obj = (JSONObject) snippets.get(label);
                final String objDesc = obj.getString("description");
                if (!objDesc.equals(description)) {
                    obj.put("description", description);
                    updated.add(label);
                }
            } else {
                final JSONObject obj = new JSONObject();
                obj.put("prefix", prefix);
                final JSONArray body = new JSONArray();
                body.put(example);
                obj.put("body", body);
                obj.put("description", description);
                snippets.put(label, obj);
                added.add(label);
            }
        }
        return snippets;
    }

    public void updateSnippets(String category, File snippetsFile, String sourceURL) throws IOException {
        final JSONObject snippets = snippetsFile.exists() ? loadFromFile(snippetsFile) : new JSONObject();
        saveToFile(snippetsFile, updateSnippets(category, snippets, sourceURL));
    }

    public void saveToFile(File file, JSONObject snippets) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        snippets.write(bw, 2, 0);
        bw.close();
    }

    public JSONObject loadFromFile(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        )) {
            return new JSONObject(new JSONTokener(br));
        }
    }

    public void printChangeLog() {
        System.out.println();
        System.out.println("----------[ CHANGELOG ]----------");
        System.out.println();
        System.out.println("Added Snippets:");
        added.stream().map(s -> "  * " + s).forEach(System.out::println);
        System.out.println();
        System.out.println("Updated Snippets:");
        updated.stream().map(s -> "  * " + s).forEach(System.out::println);
        System.out.println();
    }
}
