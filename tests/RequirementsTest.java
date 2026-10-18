/**
 * Lightweight automated checks for the core project requirements.
 * Run with:
 * javac -d out src\Course.java src\CourseManager.java tests\RequirementsTest.java
 * java -cp out RequirementsTest
 */
public class RequirementsTest {
    private static int passed;

    public static void main(String[] args) {
        testValidCourseIsNormalized();
        testInvalidCourseIsRejected();
        testDuplicateCodesAreSkippedWhenLoading();

        System.out.println("All " + passed + " requirement tests passed.");
    }

    private static void testValidCourseIsNormalized() {
        Course course = new Course(" cos201 ", "Programming I", 3);
        check("COS201".equals(course.getCourseCode()), "course code is uppercase and trimmed");
        check("Programming I".equals(course.getCourseTitle()), "course title is stored");
        check(course.getCourseUnit() == 3, "course units are stored");
    }

    private static void testInvalidCourseIsRejected() {
        expectFailure(() -> new Course("", "Title", 3));
        expectFailure(() -> new Course("COS202", "", 3));
        expectFailure(() -> new Course("COS|202", "Title", 3));
        expectFailure(() -> new Course("COS202", "Title", 0));
    }

    private static void testDuplicateCodesAreSkippedWhenLoading() {
        CourseManager manager = new CourseManager();
        manager.loadCoursesFromFile();
        check(manager.getCourseCount() == 2, "sample data loads two unique courses");
        check(manager.getTotalUnits() == 5, "sample data total is five units");
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
            throw new AssertionError("expected validation failure");
        } catch (IllegalArgumentException expected) {
            passed++;
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("Failed: " + description);
        }
        passed++;
        System.out.println("PASS: " + description);
    }
}
