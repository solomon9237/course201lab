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

/**
 * Holds courses and provides operations for managing them.
 */
public class CourseManager {
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

    /**
     * Calculates and displays the sum of the units for all stored courses.
     */
    public void computeTotalUnits() {
        int totalUnits = 0;
        for (Course course : courses) {
            totalUnits += course.getCourseUnit();
        }

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
        Path filePath = Paths.get("data", "courses.txt");
        List<Course> loadedCourses = new ArrayList<>();
        Set<String> loadedCodes = new HashSet<>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
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
            System.out.println("Loaded " + loadedCourses.size() + " course(s) from " + filePath + ".");
            if (skippedRecords > 0) {
                System.out.println("Skipped " + skippedRecords + " invalid or duplicate record(s).");
            }
        } catch (IOException exception) {
            System.out.println("Could not load courses: " + exception.getMessage());
        }
    }
    /**
     * Saves every course to data/courses.txt using code|title|units per line.
     */
    public void saveCoursesToFile() {
        Path filePath = Paths.get("data", "courses.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Course course : courses) {
                writer.write(course.getCourseCode() + "|"
                        + course.getCourseTitle() + "|"
                        + course.getCourseUnit());
                writer.newLine();
            }
            System.out.println("Saved " + courses.size() + " course(s) to " + filePath + ".");
        } catch (IOException exception) {
            System.out.println("Could not save courses: " + exception.getMessage());
        }
    }
}




