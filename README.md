# Student Course Management System

A Java-based console and local browser application for managing a student's course list. The system allows a user to add courses, view course details, search by code, calculate total units, and save or load course data from a text file.


## Overview

This project is a simple course management system designed to demonstrate core Java concepts such as:

- object-oriented programming
- data validation
- ArrayList-based storage
- recursion for display output
- reading and writing text files
- user interaction through a console menu

## Project Structure

- `src/Course.java` — represents a single course and validates its data
- `src/CourseManager.java` — stores and manages courses, performs searches and file operations
- `src/Main.java` — console interface and menu loop
- `src/WebServer.java` — local browser interface at `http://127.0.0.1:8000`
- `data/courses.txt` — saved course records in a pipe-delimited format

## Features

- Add a course with a code, title, and unit value
- Prevent duplicate course codes
- View all courses in the system
- Search a course by code (case-insensitive)
- Calculate the total units for all courses
- Update a course title and unit value
- Remove a course with confirmation
- Save courses to a file
- Load courses from a file
- Responsive browser layout for desktop, tablet, and mobile devices
- Exit the program cleanly

## How to Run

Make sure Java is installed, then open PowerShell in the project root and run:

```powershell
javac -d out src\Course.java src\CourseManager.java src\Main.java
java -cp out Main
```

## Run the Website

Compile and start the local web interface:

```powershell
javac -d out src\Course.java src\CourseManager.java src\WebServer.java
java -cp out WebServer
```


Then open [http://127.0.0.1:8000](http://127.0.0.1:8000) on the desktop. The server also prints a LAN address such as `http://172.20.10.13:8000`; open that address on a phone or tablet connected to the same Wi-Fi. You can add, view all, search by course code, update, remove, compute total units, save, load, and exit the program with the web controls. Stop the website with `Ctrl+C` in PowerShell if needed.

## Run Requirement Tests

The project includes lightweight automated checks that require no external libraries:

```powershell
javac -d out src\Course.java src\CourseManager.java tests\RequirementsTest.java
java -cp out RequirementsTest
```

## Menu Options

When the program starts, it displays this menu:

1. Add Course
2. View All Courses
3. Search Course by Code
4. Compute Total Units
5. Update Course
6. Remove Course
7. Save to File
8. Load from File
9. Exit Program

## Data File Format

The system stores courses in `data/courses.txt` using this format:

```text
COURSECODE|Course Title|Units
```

Example:

```text
COS201|Programming I|3
MTH101|Discrete Mathematics|4
ENG110|Academic Writing|2
```

## Validation Rules

The program enforces these rules:

- course code cannot be blank
- course title cannot be blank
- course code and title cannot contain `|`
- units must be a positive whole number
- course codes are stored in uppercase

## Notes

- The application runs from the project root so the data file path resolves correctly.
- Loading a file replaces the current in-memory course list with the valid entries from the file.
- Invalid or duplicate records are skipped with a message instead of crashing the program.
