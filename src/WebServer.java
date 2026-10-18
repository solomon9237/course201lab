import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** Starts a small local browser interface for the course manager. */
public class WebServer {
    private static final int PORT = 8000;
    private final CourseManager manager = new CourseManager();
    private HttpServer server;

    public static void main(String[] args) throws IOException {
        WebServer application = new WebServer();
        application.manager.loadCoursesFromFile();

        application.server = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
        application.server.createContext("/", application::handleRequest);
        application.server.setExecutor(null);
        application.server.start();
        System.out.println("Course Manager website running at http://127.0.0.1:" + PORT);
        System.out.println("Press Ctrl+C to stop the server.");
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String message = "";
        boolean shouldExit = false;
        String view = "dashboard";
        try {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseForm(exchange);
                view = form.getOrDefault("view", "dashboard");
                shouldExit = "exit".equals(form.get("action"));
                message = shouldExit ? "Program exited. You can close this browser tab." : performAction(form);
            } else {
                view = queryValue(exchange.getRequestURI().getRawQuery(), "view");
            }
            sendHtml(exchange, page(message, view));
        } catch (IllegalArgumentException exception) {
            sendHtml(exchange, page("Error: " + exception.getMessage(), view));
        } finally {
            exchange.close();
        }
        if (shouldExit) {
            server.stop(0);
        }
    }

    private String queryValue(String query, String name) {
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2 && name.equals(parts[0])) {
                    return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                }
            }
        }
        return "dashboard";
    }

    private String performAction(Map<String, String> form) {
        String action = form.getOrDefault("action", "");
        if ("add".equals(action)) {
            manager.addCourse(form.get("code"), form.get("title"), parseUnits(form));
            return "Course added successfully.";
        }
        if ("update".equals(action)) {
            manager.updateCourse(form.get("code"), form.get("title"), parseUnits(form));
            return "Course updated successfully.";
        }
        if ("remove".equals(action)) {
            manager.removeCourse(form.get("code"));
            return "Course removed successfully.";
        }
        if ("save".equals(action)) {
            manager.saveCoursesToFile();
            return "Courses saved to data/courses.txt.";
        }
        if ("load".equals(action)) {
            manager.loadCoursesFromFile();
            return "Courses loaded from data/courses.txt.";
        }
        if ("search".equals(action)) {
            Course course = findCourse(form.get("code"));
            return course == null ? "No course found with code " + form.get("code") + "."
                    : "Found: " + course;
        }
        return "Unknown action.";
    }

    private Course findCourse(String code) {
        if (code != null) {
            for (Course course : manager.getCourses()) {
                if (course.getCourseCode().equalsIgnoreCase(code.trim())) {
                    return course;
                }
            }
        }
        return null;
    }

    private int parseUnits(Map<String, String> form) {
        try {
            return Integer.parseInt(form.getOrDefault("units", "").trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Units must be a positive whole number.");
        }
    }

    private Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> values = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                values.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return values;
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private String page(String message, String view) {
        StringBuilder rows = new StringBuilder();
        for (Course course : manager.getCourses()) {
            rows.append("<tr><td>").append(escape(course.getCourseCode())).append("</td><td>")
                    .append(escape(course.getCourseTitle())).append("</td><td>")
                    .append(course.getCourseUnit()).append("</td></tr>");
        }
        if (rows.length() == 0) {
            rows.append("<tr><td colspan='3'>No courses available.</td></tr>");
        }
        String content;
        if ("add".equals(view)) {
            content = formCard("Add New Course", "add", "Course code", "Course title", "Units", "Add Course");
        } else if ("search".equals(view)) {
            content = "<section class='card'><h2>Search Course</h2><p>Find a course by its code.</p>"
                    + "<form method='post'><input type='hidden' name='action' value='search'><input type='hidden' name='view' value='search'>"
                    + "<label>Course code</label><input name='code' placeholder='e.g. COS201' required><button>Search Course</button></form></section>";
        } else if ("update".equals(view)) {
            content = formCard("Update Course", "update", "Existing course code", "New course title", "New units", "Update Course");
        } else if ("remove".equals(view)) {
            content = "<section class='card'><h2>Remove Course</h2><p>Remove a course from the current list.</p>"
                    + "<form method='post' onsubmit=\"return confirm('Remove this course?')\"><input type='hidden' name='action' value='remove'><input type='hidden' name='view' value='remove'>"
                    + "<label>Course code</label><input name='code' placeholder='e.g. COS201' required><button class='danger'>Remove Course</button></form></section>";
        } else {
            content = "<section class='stats'><div><strong>" + manager.getCourseCount() + "</strong><span>Courses</span></div>"
                    + "<div><strong>" + manager.getTotalUnits() + "</strong><span>Total units</span></div></section>"
                    + "<section class='card'><h2>All Courses</h2><table><tr><th>Code</th><th>Title</th><th>Units</th></tr>" + rows + "</table></section>";
        }
        return "<!doctype html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1'><title>Student Course Manager</title>"
                + "<style>*{box-sizing:border-box}body{margin:0;font-family:Arial,sans-serif;background:#f5f6fb;color:#20253a;display:flex;min-height:100vh}.sidebar{width:245px;background:#11182b;color:white;padding:34px 18px;flex-shrink:0}.brand{font-size:21px;font-weight:bold;margin:0 12px 5px}.subtitle{color:#9ba5c2;font-size:12px;margin:0 12px 48px}.nav a{display:block;color:#c5cbe0;text-decoration:none;padding:14px 16px;border-radius:7px;margin:5px 0}.nav a:hover,.nav a.active{background:#27245d;color:white;border-left:3px solid #6555f4}.main{flex:1;padding:34px 46px;max-width:1100px}.topbar{display:flex;justify-content:space-between;align-items:center;margin-bottom:35px}.topbar h1{font-size:29px;margin:0 0 6px}.topbar p{color:#7f8498;margin:0}.top-actions form{display:inline}.top-actions button{background:white;color:#60667d;border:1px solid #ddd}.top-actions button.primary{background:#5948e8;color:white;border:0}.card{background:white;padding:27px;border-radius:5px;box-shadow:0 2px 8px #e0e1e8;margin-bottom:20px;max-width:760px}.card h2{margin-top:0;font-size:18px}.card p{color:#72778a}.card form{display:flex;flex-direction:column;gap:8px;max-width:650px}.card label{font-size:11px;text-transform:uppercase;color:#74798d;font-weight:bold;margin-top:8px}.card input{padding:13px;border:1px solid #e1e2e9;border-radius:5px;font-size:14px}.button,button{padding:13px 18px;border:0;border-radius:5px;background:#5948e8;color:white;font-weight:bold;cursor:pointer;margin-top:10px}button.danger{background:#d94b5b}.stats{display:flex;gap:18px;margin-bottom:20px}.stats div{background:white;padding:22px 30px;border-radius:5px;box-shadow:0 2px 8px #e0e1e8;min-width:170px}.stats strong{display:block;font-size:28px;color:#5948e8}.stats span{color:#7f8498;font-size:13px}table{width:100%;border-collapse:collapse}th,td{text-align:left;padding:13px;border-bottom:1px solid #eee}th{font-size:12px;color:#7f8498;text-transform:uppercase}.message{background:#e3f4ec;color:#21734a;padding:13px;border-radius:5px;max-width:760px;margin-bottom:20px}@media(max-width:700px){.sidebar{width:180px}.main{padding:24px}.topbar{display:block}.top-actions{margin-top:15px}}"
                + "</style></head><body><aside class='sidebar'><p class='brand'>COS 201</p><p class='subtitle'>Course Manager</p><nav class='nav'>"
                + navLink("dashboard", "Dashboard", view) + navLink("add", "Add Course", view) + navLink("search", "Search Course", view)
                + navLink("update", "Update Course", view) + navLink("remove", "Remove Course", view) + "</nav></aside><main class='main'>"
                + "<header class='topbar'><div><h1>Student Course Management</h1><p>COS 201 Programming I &nbsp;|&nbsp; Lab Assessment Mini-System</p></div><div class='top-actions'>"
                + "<form method='post'><button name='action' value='load'>Load</button><button class='primary' name='action' value='save'>Save</button></form></div></header>"
                + (message.isEmpty() ? "" : "<p class='message'>" + escape(message) + "</p>") + content + "</main></body></html>";
    }

    private String navLink(String target, String label, String selected) {
        return "<a class='" + (target.equals(selected) ? "active" : "") + "' href='/?view=" + target + "'>" + label + "</a>";
    }

    private String formCard(String heading, String action, String codeLabel, String titleLabel, String unitsLabel, String buttonText) {
        return "<section class='card'><h2>" + heading + "</h2><p>Enter the course details below.</p><form method='post'>"
                + "<input type='hidden' name='action' value='" + action + "'><input type='hidden' name='view' value='" + action + "'>"
                + "<label>" + codeLabel + "</label><input name='code' placeholder='e.g. COS201' required><label>" + titleLabel + "</label>"
                + "<input name='title' placeholder='e.g. Programming I' required><label>" + unitsLabel + "</label><input name='units' type='number' min='1' placeholder='e.g. 3' required><button>" + buttonText + "</button></form></section>";
    }

    private String escape(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }
}
