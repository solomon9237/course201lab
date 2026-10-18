import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Collections;

/**
 * Holds courses and provides operations for managing them.
 */
public class CourseManager {
    private static final Path DATA_FILE = Paths.get("data", "courses.txt");
    private final List<Course> courses = new ArrayList<>();

    /**
     * Prompts for a course's details, validates them, and stores the course.
     */
    public void addCourse(Scanner scanner) {
        System.out.print("Enter course code: ");
        String code = scanner.nextLine().trim();

        System.out.print("Enter course title: ");
        String title = scanner.nextLine().trim();

        if (code.isEmpty() || title.isEmpty()) {
            System.out.println("Course code and title cannot be blank. Course was not added.");
            return;
        }
        if (code.contains("|") || title.contains("|")) {
            System.out.println("Course code and title cannot contain the | character. Course was not added.");
            return;
        }

        String normalizedCode = code.toUpperCase(Locale.ROOT);
        for (Course course : courses) {
            if (course.getCourseCode().equals(normalizedCode)) {
                System.out.println("A course with that code already exists.");
                return;
            }
        }

        int units;
        while (true) {
            System.out.print("Enter course unit (positive whole number): ");
            String unitInput = scanner.nextLine().trim();
            try {
                units = Integer.parseInt(unitInput);
                if (units > 0) {
                    break;
                }
                System.out.println("Units must be greater than zero. Try again.");
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number. Try again.");
            }
        }

        Course course = new Course(code, title, units);
        courses.add(course);
        System.out.println("Course added successfully: " + course);
    }

    /**
     * Displays all stored courses, or an empty-list message.
     */
    public void displayAllCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses have been added yet.");
            return;
        }

        System.out.println("\n--- Course List ---");
        displayCoursesRecursively(0);
    }

    /**
     * Displays courses recursively, beginning at the supplied list index.
     * The base case stops when the index reaches the list size.
     */
    private void displayCoursesRecursively(int index) {
        if (index >= courses.size()) {
            return;
        }

        System.out.println((index + 1) + ". " + courses.get(index));
        displayCoursesRecursively(index + 1);
    }

    /**
     * Finds and displays a course using its code, ignoring letter casing.
     */
    public void searchCourseByCode(Scanner scanner) {
        if (courses.isEmpty()) {
            System.out.println("There are no courses to search.");
            return;
        }

        System.out.print("Enter the course code to search for: ");
        String searchCode = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
        if (searchCode.isEmpty()) {
            System.out.println("Course code cannot be blank.");
            return;
        }

        for (Course course : courses) {
            if (course.getCourseCode().equals(searchCode)) {
                System.out.println("Course found: " + course);
                return;
            }
        }

        System.out.println("No course found with code " + searchCode + ".");
    }

    /** Updates the title and units for an existing course. */
    public void updateCourse(Scanner scanner) {
        if (courses.isEmpty()) {
            System.out.println("There are no courses to update.");
            return;
        }

        System.out.print("Enter the course code to update: ");
        String code = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
        Course existing = findCourse(code);
        if (existing == null) {
            System.out.println("No course found with code " + code + ".");
            return;
        }

        System.out.print("Enter new course title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty() || title.contains("|")) {
            System.out.println("Course title must be non-blank and cannot contain |.");
            return;
        }

        System.out.print("Enter new course unit (positive whole number): ");
        String unitInput = scanner.nextLine().trim();
        try {
            int units = Integer.parseInt(unitInput);
            if (units <= 0) {
                throw new NumberFormatException();
            }
            courses.set(courses.indexOf(existing), new Course(code, title, units));
            System.out.println("Course updated successfully.");
        } catch (NumberFormatException exception) {
            System.out.println("Units must be a positive whole number. Course was not updated.");
        }
    }

    /** Removes a course by code after asking for confirmation. */
    public void removeCourse(Scanner scanner) {
        if (courses.isEmpty()) {
            System.out.println("There are no courses to remove.");
            return;
        }

        System.out.print("Enter the course code to remove: ");
        String code = scanner.nextLine().trim().toUpperCase(Locale.ROOT);
        Course existing = findCourse(code);
        if (existing == null) {
            System.out.println("No course found with code " + code + ".");
            return;
        }

        System.out.print("Remove " + existing.getCourseTitle() + "? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            courses.remove(existing);
            System.out.println("Course removed successfully.");
        } else {
            System.out.println("Course was not removed.");
        }
    }

    private Course findCourse(String code) {
        String normalizedCode = code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
        for (Course course : courses) {
            if (course.getCourseCode().equals(normalizedCode)) {
                return course;
            }
        }
        return null;
    }

    /** Adds a course for non-console clients such as the web interface. */
    public void addCourse(String code, String title, int units) {
        if (findCourse(code) != null) {
            throw new IllegalArgumentException("A course with that code already exists.");
        }
        courses.add(new Course(code, title, units));
    }

    /** Updates a course for non-console clients such as the web interface. */
    public void updateCourse(String code, String title, int units) {
        Course existing = findCourse(code);
        if (existing == null) {
            throw new IllegalArgumentException("No course found with code " + code + ".");
        }
        courses.set(courses.indexOf(existing), new Course(existing.getCourseCode(), title, units));
    }

    /** Removes a course for non-console clients such as the web interface. */
    public void removeCourse(String code) {
        Course existing = findCourse(code);
        if (existing == null) {
            throw new IllegalArgumentException("No course found with code " + code + ".");
        }
        courses.remove(existing);
    }

    /** Provides a read-only view of the current courses for display. */
    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    /** Returns the number of courses currently held in memory. */
    public int getCourseCount() {
        return courses.size();
    }

    /** Returns the total units currently held in memory. */
    public int getTotalUnits() {
        int totalUnits = 0;
        for (Course course : courses) {
            totalUnits += course.getCourseUnit();
        }
        return totalUnits;
    }

    /**
     * Calculates and displays the sum of the units for all stored courses.
     */
    public void computeTotalUnits() {
        int totalUnits = getTotalUnits();

        if (courses.isEmpty()) {
            System.out.println("No courses have been added, so the total is 0 units.");
        } else {
            System.out.println("Total units across " + courses.size()
                    + " course(s): " + totalUnits);
        }
    }

    /**
     * Loads courses from data/courses.txt, replacing the current course list.
     * Invalid records are skipped with a message.
     */
    public void loadCoursesFromFile() {
        List<Course> loadedCourses = new ArrayList<>();
        Set<String> loadedCodes = new HashSet<>();

        try {
            List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
            int skippedRecords = 0;

            for (int index = 0; index < lines.size(); index++) {
                String[] fields = lines.get(index).split("\\|", -1);
                if (fields.length != 3) {
                    System.out.println("Skipping invalid record on line " + (index + 1) + ".");
                    skippedRecords++;
                    continue;
                }

                try {
                    String code = fields[0].trim();
                    String title = fields[1].trim();
                    int units = Integer.parseInt(fields[2].trim());
                    Course course = new Course(code, title, units);

                    if (!loadedCodes.add(course.getCourseCode())) {
                        System.out.println("Skipping duplicate course code on line " + (index + 1) + ".");
                        skippedRecords++;
                        continue;
                    }
                    loadedCourses.add(course);
                } catch (IllegalArgumentException exception) {
                    System.out.println("Skipping invalid course on line " + (index + 1) + ".");
                    skippedRecords++;
                }
            }

            courses.clear();
            courses.addAll(loadedCourses);
            System.out.println("Loaded " + loadedCourses.size() + " course(s) from " + DATA_FILE + ".");
            if (skippedRecords > 0) {
                System.out.println("Skipped " + skippedRecords + " invalid or duplicate record(s).");
            }
        } catch (IOException exception) {
            System.out.println("Could not load courses from " + DATA_FILE + ": " + exception.getMessage());
        }
    }
    /**
     * Saves every course to data/courses.txt using code|title|units per line.
     */
    public void saveCoursesToFile() {
        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            System.out.println("Could not prepare the data folder: " + exception.getMessage());
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE, StandardCharsets.UTF_8)) {
            for (Course course : courses) {
                writer.write(course.getCourseCode() + "|"
                        + course.getCourseTitle() + "|"
                        + course.getCourseUnit());
                writer.newLine();
            }
            System.out.println("Saved " + courses.size() + " course(s) to " + DATA_FILE + ".");
        } catch (IOException exception) {
            System.out.println("Could not save courses: " + exception.getMessage());
        }
    }
}




