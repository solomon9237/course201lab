import java.util.Scanner;

/**
 * Starts and controls the Student Course Management Mini-System.
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CourseManager courseManager = new CourseManager();
        boolean running = true;

        System.out.println("Welcome to the Student Course Management Mini-System");

        while (running) {
            displayMenu();
            System.out.print("Choose an option (1-9): ");
            if (!scanner.hasNextLine()) {
                System.out.println("\nInput ended. Goodbye!");
                break;
            }
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid choice. Enter a number from 1 to 9.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    courseManager.addCourse(scanner);
                    break;
                case 2:
                    courseManager.displayAllCourses();
                    break;
                case 3:
                    courseManager.searchCourseByCode(scanner);
                    break;
                case 4:
                    courseManager.computeTotalUnits();
                    break;
                case 5:
                    courseManager.updateCourse(scanner);
                    break;
                case 6:
                    courseManager.removeCourse(scanner);
                    break;
                case 7:
                    courseManager.saveCoursesToFile();
                    break;
                case 8:
                    courseManager.loadCoursesFromFile();
                    break;
                case 9:
                    running = false;
                    System.out.println("Thank you for using the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number from 1 to 9.");
            }
            System.out.println();
        }

        scanner.close();
    }

    /** Prints the available program actions. */
    private static void displayMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println("1. Add Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Search Course by Code");
        System.out.println("4. Compute Total Units");
        System.out.println("5. Update Course");
        System.out.println("6. Remove Course");
        System.out.println("7. Save to File");
        System.out.println("8. Load from File");
        System.out.println("9. Exit Program");
    }
}
