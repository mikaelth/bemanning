package se.uu.ebc.bemanning.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Harvests the "Biologikurser periodvis" page and extracts, per course, which
 * teaching period(s) the course runs in.
 *
 * <p>The source page (configured via {@code courseperiods.url}) lists courses
 * grouped under period headings ("HÖSTTERMINEN PERIOD 1", "PERIOD 2",
 * "VÅRTERMINEN PERIOD 3", "PERIOD 4"). Each course line carries a course code
 * such as {@code 1BG100}, {@code 1KB103} or {@code 1MA071}. A course may appear
 * under several periods (e.g. a thesis course running all year), so periods are
 * collected per code.
 */
@Service
@Slf4j
public class CoursePeriodService {

    @Value("${courseperiods.url}")
    private String coursePeriodsUrl;

    @Value("${terms.url}")
    private String termsUrl;

    // Course codes look like 1BG100, 1KB103, 1MA071, 1MB205: a leading digit,
    // two uppercase letters, three digits.
    private static final Pattern COURSE_CODE = Pattern.compile("\\b\\d[A-ZÅÄÖ]{2}\\d{3}\\b");
    // Period markers on the page, e.g. "PERIOD 1" ... "PERIOD 4".
    private static final Pattern PERIOD_MARKER = Pattern.compile("PERIOD\\s*([1-4])");

    // The faculty whose period dates we extract.
    private static final String TECHNAT_FACULTY = "Teknisk-naturvetenskapliga fakulteten";

    // Faculty section headings on the page, e.g. "Juridiska fakulteten". Used to
    // bound the Teknisk-naturvetenskapliga section at the next faculty heading,
    // since UU may reorder the faculties (so it is not necessarily last).
    // Matches a faculty name (one capitalised, possibly hyphenated word) directly
    // followed by "fakulteten", e.g. "Juridiska fakulteten",
    // "Historisk-filosofiska fakulteten". Deliberately narrow so it only matches
    // real faculty headings, not arbitrary preceding text.
    private static final Pattern FACULTY_HEADING =
            Pattern.compile("[A-ZÅÄÖ][\\p{L}-]*\\s+fakulteten");

    // Term heading, e.g. "Höstterminen 2026" / "Vårterminen 2027".
    private static final Pattern TERM_HEADING =
            Pattern.compile("(Höstterminen|Vårterminen)\\s+(\\d{4})");

    // A period entry: "2026-08-31 – 2026-11-01 (1)". The trailing "(n)" is
    // optional (later terms on the page omit it). The separator is an en dash,
    // optionally surrounded by spaces.
    private static final Pattern PERIOD_ENTRY = Pattern.compile(
            "(\\d{4}-\\d{2}-\\d{2})\\s*[–-]\\s*(\\d{4}-\\d{2}-\\d{2})(?:\\s*\\(([1-4A-D])\\))?");

    /**
     * A course and the set of teaching periods it runs in.
     *
     * @param courseCode the course code, e.g. "1BG100"
     * @param periods    the periods (1..4) the course runs in, ascending
     */
    public record CoursePeriods(String courseCode, SortedSet<Integer> periods) {

        /**
         * The periods formatted for display, e.g. "Period 1" or "Period 2, 3, 4".
         * Returns an empty string when no periods are known.
         */
        public String periodsAsString() {
            if (periods == null || periods.isEmpty()) {
                return "";
            }
            String joined = periods.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            // e.g. "Period 1" or "Perioder 2, 3, 4".
            return (periods.size() == 1 ? "Period " : "Perioder ") + joined;
        }
    }

    /**
     * Fetches and parses the page, returning one {@link CoursePeriods} per course
     * code found, in the order the codes first appear.
     *
     * @throws IOException if the page cannot be fetched
     */
    public List<CoursePeriods> getCoursePeriods() throws IOException {
        Document doc = Jsoup.connect(coursePeriodsUrl)
                .userAgent("Mozilla/5.0")
                .timeout(15 * 1000)
                .get();

        // Work on the page's visible text in document order. Period headings and
        // course lines run together in the markup, so a linear scan over the text
        // (tracking the current period as we pass each "PERIOD n" marker) is the
        // most robust approach.
        String text = doc.body() == null ? "" : doc.body().text();

        // code -> periods, preserving first-seen order of codes.
        Map<String, SortedSet<Integer>> byCode = new LinkedHashMap<>();

        int currentPeriod = -1;
        // Scan the text for period markers and course codes in order of position.
        Matcher period = PERIOD_MARKER.matcher(text);
        Matcher code = COURSE_CODE.matcher(text);

        int periodPos = period.find() ? period.start() : -1;
        int codePos = code.find() ? code.start() : -1;

        while (periodPos >= 0 || codePos >= 0) {
            // Advance whichever match comes first in the text.
            boolean takePeriod = codePos < 0 || (periodPos >= 0 && periodPos < codePos);
            if (takePeriod) {
                currentPeriod = Integer.parseInt(period.group(1));
                periodPos = period.find() ? period.start() : -1;
            } else {
                String courseCode = code.group();
                if (currentPeriod >= 1) {
                    byCode.computeIfAbsent(courseCode, k -> new TreeSet<>()).add(currentPeriod);
                }
                codePos = code.find() ? code.start() : -1;
            }
        }

        List<CoursePeriods> result = new ArrayList<>(byCode.size());
        byCode.forEach((courseCode, periods) -> result.add(new CoursePeriods(courseCode, periods)));

        log.debug("Harvested {} course codes with periods from {}", result.size(), coursePeriodsUrl);
        return result;
    }

    /**
     * The start/end dates for one teaching period of a given year.
     *
     * @param period the period number (1..4)
     * @param year   the calendar year the term runs in (e.g. an autumn-term
     *               period in 2026 has year 2026; a spring-term period in 2027
     *               has year 2027)
     * @param start  first day of the period
     * @param end    last day of the period
     */
    public record PeriodDates(int period, int year, LocalDate start, LocalDate end) {

        /** e.g. "Period 1 2026: 2026-08-31 – 2026-11-01". */
        public String asString() {
            return "Period " + period + " " + year + ": " + start + " – " + end;
        }
    }

    /**
     * Harvests the "Läsår och terminer" page (configured via {@code terms.url})
     * and returns the teaching-period date ranges for the
     * <em>Teknisk-naturvetenskapliga fakulteten</em>.
     *
     * <p>Within that faculty's section the page lists term headings
     * ("Höstterminen 2026", "Vårterminen 2027", ...) followed by two date ranges
     * each. Autumn-term ranges are periods 1 and 2, spring-term ranges are
     * periods 3 and 4. Some ranges carry an explicit "(n)" label and some do not;
     * when absent the period is inferred from the term (autumn/spring) and the
     * range's position within the term.
     *
     * @throws IOException if the page cannot be fetched
     */
    public List<PeriodDates> getTechNatPeriods() throws IOException {
        Document doc = Jsoup.connect(termsUrl)
                .userAgent("Mozilla/5.0")
                .timeout(15 * 1000)
                .get();

        String text = doc.body() == null ? "" : doc.body().text();

        // Isolate the Teknisk-naturvetenskapliga faculty section. UU reorders the
        // faculties, so bound it from its heading to the next faculty heading
        // (not to the end of the page). Fall back to end-of-page if it is last.
        int start = text.indexOf(TECHNAT_FACULTY);
        if (start < 0) {
            log.warn("Faculty section '{}' not found at {}", TECHNAT_FACULTY, termsUrl);
            return List.of();
        }
        int contentStart = start + TECHNAT_FACULTY.length();
        int sectionEnd = text.length();
        Matcher faculty = FACULTY_HEADING.matcher(text);
        if (faculty.find(contentStart)) {
            sectionEnd = faculty.start();
        }
        String section = text.substring(contentStart, sectionEnd);

        List<PeriodDates> result = new ArrayList<>();

        // Walk term headings and the period entries that follow each, in order.
        Matcher term = TERM_HEADING.matcher(section);
        Matcher entry = PERIOD_ENTRY.matcher(section);

        int entryPos = entry.find() ? entry.start() : -1;

        boolean autumn = true;
        int year = -1;
        int positionInTerm = 0;

        int termPos = term.find() ? term.start() : -1;

        while (entryPos >= 0) {
            // Apply any term heading(s) that occur before the next entry.
            while (termPos >= 0 && termPos < entryPos) {
                autumn = "Höstterminen".equals(term.group(1));
                year = Integer.parseInt(term.group(2));
                positionInTerm = 0;
                termPos = term.find() ? term.start() : -1;
            }

            if (year > 0) {
                positionInTerm++;
                int period = resolvePeriod(entry.group(3), autumn, positionInTerm);
                if (period > 0) {
                    result.add(new PeriodDates(
                            period, year,
                            LocalDate.parse(entry.group(1)),
                            LocalDate.parse(entry.group(2))));
                }
            }
            entryPos = entry.find() ? entry.start() : -1;
        }

        log.debug("Harvested {} TeknNat period date ranges from {}", result.size(), termsUrl);
        return result;
    }

    /**
     * Resolves the period number for an entry. Uses the explicit label when it is
     * a digit 1..4; otherwise infers it from the term and the entry's position
     * (autumn: 1,2; spring: 3,4). Letter labels (A..D), used by other faculties,
     * are ignored for this numeric scheme and fall back to inference.
     */
    private int resolvePeriod(String label, boolean autumn, int positionInTerm) {
        if (label != null && label.length() == 1 && Character.isDigit(label.charAt(0))) {
            return label.charAt(0) - '0';
        }
        // Infer: autumn term -> periods 1,2 ; spring term -> periods 3,4.
        if (positionInTerm == 1) {
            return autumn ? 1 : 3;
        }
        if (positionInTerm == 2) {
            return autumn ? 2 : 4;
        }
        return -1;
    }
}
