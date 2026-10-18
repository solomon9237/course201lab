import java.util.Locale;

/**
 * Represents one course in the course management system.
 */
public class Course {
    private final String courseCode;
    private final String courseTitle;
    private final int courseUnit;

    /**
     * Creates a course after cleaning and validating its details.
     *
     * @param courseCode  the course identifier, stored in uppercase
     * @param courseTitle the course name
     * @param courseUnit  the positive number of units
     */
    public Course(String courseCode, String courseTitle, int courseUnit) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be blank.");
        }
        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be blank.");
        }
        if (courseCode.indexOf('|') >= 0 || courseTitle.indexOf('|') >= 0) {
            throw new IllegalArgumentException("Course code and title cannot contain the | character.");
        }
        if (courseUnit <= 0) {
            throw new IllegalArgumentException("Course units must be greater than zero.");
        }

        this.courseCode = courseCode.trim().toUpperCase(Locale.ROOT);
        this.courseTitle = courseTitle.trim();
        this.courseUnit = courseUnit;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getCourseUnit() {
        return courseUnit;
    }

    @Override
    public String toString() {
        return courseCode + " | " + courseTitle + " | " + courseUnit + " units";
    }
}
