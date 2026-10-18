# Student Course Management Mini-System

**Course:** COS 201 Programming I  
**Assessment:** Mid-Semester Lab Assessment  
**Language:** Java

A course management application with a console interface and an optional local browser interface. The project follows the requirements checklist supplied for the assessment.

## Project structure

- `src/Course.java` - course data model and validation
- `src/CourseManager.java` - console features, collection handling, file I/O, and recursive listing
- `src/Main.java` - seven-option console menu
- `src/WebServer.java` - browser interface at `http://127.0.0.1:8000`
- `data/courses.txt` - saved course records in `code|title|units` format
- `docs/assessment-report.md` - report draft; fill in your name and date and personalize the challenges section
- `docs/demo-checklist.md` - run and screenshot steps
- `docs/submission-checklist.md` - assessment requirement map and final items to submit

## Run the console version

From the project root in PowerShell:

```powershell
javac -d out src\Course.java src\CourseManager.java src\Main.java
java -cp out Main
```

## Run the browser version

Compile and start the local Java server:

```powershell
javac -d out src\Course.java src\CourseManager.java src\Main.java src\WebServer.java
java -cp out WebServer
```

Open `http://127.0.0.1:8000` in a browser. Stop the server with **Ctrl+C** in its terminal. It listens on the local computer only. Run it from the project root so the relative data-file path works.
