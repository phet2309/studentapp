import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CourseServlet")
public class CourseServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/njit";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "admin123";
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            if (request.getParameter("semester") != null) {
                String selectedSemester = request.getParameter("semester");
                String query = "SELECT course_code, semester, course_name FROM courses " +
                        "WHERE semester = ?";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, selectedSemester);

                ResultSet resultSet = preparedStatement.executeQuery();

                out.println("<html><head><title>NJIT Registration System</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"./resources/styles.css\">\n</head><body>");
                if(resultSet.next())
                    out.println("<h1>Courses for " + selectedSemester + "</h1>");
                else
                    out.println("<h1>Invalid Selection</h1>");

                while (resultSet.next()) {
                    String courseCode = resultSet.getString("course_code");
                    String semester = resultSet.getString("semester");
                    String courseName = resultSet.getString("course_name");

                    out.println("<div class=\"message-div\">\n" +
                            courseCode + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + semester + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + courseName + "</div>");

                }
                out.println("</body></html>");


            } else if (request.getParameter("courseID") != null) {
                String courseID = request.getParameter("courseID");
                String semester1 = request.getParameter("semester1");

                String query = "SELECT course_name FROM courses " +
                        "WHERE course_code = ? AND semester = ?";

                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, courseID);
                preparedStatement.setString(2, semester1);

                ResultSet resultSet = preparedStatement.executeQuery();

                out.println("<html><head><title>NJIT Registration System</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"./resources/styles.css\">\n</head><body>");
                if (resultSet.next()) {
                    String courseName = resultSet.getString("course_name");
                    out.println("<h1>You are registered in " + courseName + " for " + semester1 + ".</h1>");
                    out.println("</body></html>");
                } else {
                    out.println("<h1>The course is not offered.</h1>");
                    out.println("</body></html>");
                }
            } else {
                out.println("<html><head><title>NJIT Registration System</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"./resources/styles.css\">\n</head><body>");
                out.println("<h1>Invalid request.</h1>");
                out.println("</body></html>");
            }
        } catch (SQLException | ClassNotFoundException e) {
            out.println("<html><head><title>NJIT Registration System</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"./resources/styles.css\">\n</head><body>");
            out.println("<h1>" + e.getMessage() + "</h1>");
            out.println("</body></html>");
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
