package org.example.studentmanagement;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.example.studentmanagement.component.StatusMessage;
import org.example.studentmanagement.model.Course;
import org.example.studentmanagement.model.Enrollment;
import org.example.studentmanagement.model.StudentManager;
import org.example.studentmanagement.model.UserAccount;
import org.example.studentmanagement.model.UserRole;
import org.example.studentmanagement.model.Teacher;

import java.util.List;

public class HelloController {
    private final StudentManager manager = new StudentManager();
    private UserAccount currentUser;

    @FXML
    private Label statusText;

    @FXML private Pane authPane;
    @FXML private VBox signupPane;
    @FXML private VBox supervisorPane;
    @FXML private VBox teacherPane;
    @FXML private VBox studentPane;

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;

    @FXML private TextField signupFullNameField;
    @FXML private TextField signupAgeField;
    @FXML private TextField signupUsernameField;
    @FXML private PasswordField signupPasswordField;
    @FXML private TextField supervisorTeacherNameField;
    @FXML private TextField supervisorTeacherAgeField;
    @FXML private TextField supervisorTeacherUsernameField;
    @FXML private PasswordField supervisorTeacherPasswordField;
    @FXML private TextField supervisorTeacherSubjectField;
    @FXML private TextField supervisorCourseNameField;
    @FXML private TextField supervisorRemoveTeacherUsernameField;
    @FXML private TextField supervisorSearchTeacherField;
    @FXML private javafx.scene.control.ChoiceBox<String> supervisorAssignTeacherChoice;

    @FXML private TableView<Teacher> supervisorTeachersTable;
    @FXML private TableColumn<Teacher, String> colSupTeacherUsername;
    @FXML private TableColumn<Teacher, String> colSupTeacherName;
    @FXML private TableColumn<Teacher, String> colSupTeacherSubject;

    @FXML private TableView<Course> supervisorCoursesTable;
    @FXML private TableColumn<Course, String> colSupCourseName;
    @FXML private TableColumn<Course, String> colSupCourseTeacher;

    @FXML private TableView<Enrollment> teacherEnrollmentsTable;
    @FXML private TableColumn<Enrollment, String> colTeacherStudent;
    @FXML private TableColumn<Enrollment, String> colTeacherCourse;
    @FXML private TableColumn<Enrollment, String> colTeacherMarks;
    @FXML private TextField teacherGradeStudentField;
    @FXML private TextField teacherGradeCourseField;
    @FXML private TextField teacherGradeMarksField;

    @FXML private javafx.scene.control.ChoiceBox<String> studentCourseChoice;
    @FXML private TableView<Enrollment> studentMyCoursesTable;
    @FXML private TableColumn<Enrollment, String> colStudentCourse;
    @FXML private TableColumn<Enrollment, String> colStudentTeacher;
    @FXML private TableColumn<Enrollment, String> colStudentMarks;

    @FXML
    private void initialize() {
        colSupTeacherUsername.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        colSupTeacherName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colSupTeacherSubject.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getSubject()));
        colSupCourseName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourseName()));
        colSupCourseTeacher.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTeacherUsername()));

        colTeacherStudent.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStudentUsername()));
        colTeacherCourse.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourseName()));
        colTeacherMarks.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMarksText()));

        colStudentCourse.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourseName()));
        colStudentTeacher.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTeacherUsername()));
        colStudentMarks.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getMarksText()));

        showAuth();
    }

    @FXML
    private void onLogin() {
        try {
            currentUser = manager.login(loginUsernameField.getText(), loginPasswordField.getText());
            loginPasswordField.clear();
            routeByRole();
            setStatus("Login successful. Welcome " + currentUser.getFullName() + ".");
        } catch (Exception ex) {
            setStatus(ex.getMessage());
        }
    }

    @FXML
    private void onSignUp() {
        try {
            manager.signUpStudent(
                    signupFullNameField.getText(),
                    signupUsernameField.getText(),
                    signupPasswordField.getText(),
                    Integer.parseInt(signupAgeField.getText())
            );
            clearAuthSignup();
            showAuth();
            setStatus("Student signup successful. You can now login.");
        } catch (Exception ex) {
            setStatus("Signup error: " + ex.getMessage());
        }
    }

    @FXML
    private void onOpenSignup() {
        showOnly(signupPane);
    }

    @FXML
    private void onBackToLogin() {
        showAuth();
    }

    @FXML
    private void onCreateTeacher() {
        try {
            manager.addTeacherBySupervisor(
                    currentUser.getUsername(),
                    supervisorTeacherNameField.getText(),
                    supervisorTeacherUsernameField.getText(),
                    supervisorTeacherPasswordField.getText(),
                    Integer.parseInt(supervisorTeacherAgeField.getText()),
                    supervisorTeacherSubjectField.getText()
            );
            supervisorTeacherNameField.clear();
            supervisorTeacherAgeField.clear();
            supervisorTeacherUsernameField.clear();
            supervisorTeacherPasswordField.clear();
            supervisorTeacherSubjectField.clear();
            refreshSupervisorData();
            setStatus("Teacher created successfully.");
        } catch (Exception ex) {
            setStatus("Create teacher error: " + ex.getMessage());
        }
    }

    @FXML
    private void onAddTeacher() {
        onCreateTeacher();
    }

    @FXML
    private void onRemoveTeacher() {
        try {
            String teacherUsername = supervisorRemoveTeacherUsernameField.getText() == null
                    ? ""
                    : supervisorRemoveTeacherUsernameField.getText().trim();
            if (teacherUsername.isEmpty()) {
                throw new IllegalArgumentException("Please enter a teacher username to remove.");
            }

            manager.removeTeacherBySupervisor(currentUser.getUsername(), teacherUsername);
            supervisorRemoveTeacherUsernameField.clear();
            refreshSupervisorData();
            setStatus("Teacher removed successfully.");
        } catch (Exception ex) {
            setStatus("Remove teacher error: " + ex.getMessage());
        }
    }

    @FXML
    private void onSearchTeacher() {
        try {
            supervisorTeachersTable.setItems(FXCollections.observableArrayList(
                    manager.searchTeachersByKeyword(supervisorSearchTeacherField.getText())
            ));
            setStatus("Teacher search completed.");
        } catch (Exception ex) {
            setStatus("Search error: " + ex.getMessage());
        }
    }

    @FXML
    private void onSortTeachers() {
        try {
            supervisorTeachersTable.setItems(FXCollections.observableArrayList(
                    manager.getTeachersSortedByName()
            ));
            setStatus("Teachers sorted by name.");
        } catch (Exception ex) {
            setStatus("Sort error: " + ex.getMessage());
        }
    }

    @FXML
    private void onAssignCourse() {
        try {
            manager.createCourseAndAssignTeacher(
                    currentUser.getUsername(),
                    supervisorCourseNameField.getText(),
                    supervisorAssignTeacherChoice.getValue()
            );
            supervisorCourseNameField.clear();
            refreshSupervisorData();
            setStatus("Course assigned to teacher.");
        } catch (Exception ex) {
            setStatus("Assign course error: " + ex.getMessage());
        }
    }

    @FXML
    private void onTeacherAssignGrade() {
        try {
            String studentUsername = teacherGradeStudentField.getText() == null ? "" : teacherGradeStudentField.getText().trim();
            String courseName = teacherGradeCourseField.getText() == null ? "" : teacherGradeCourseField.getText().trim();
            String marksText = teacherGradeMarksField.getText() == null ? "" : teacherGradeMarksField.getText().trim();

            if (studentUsername.isEmpty() || courseName.isEmpty() || marksText.isEmpty()) {
                throw new IllegalArgumentException("Please fill student username, course name, and marks.");
            }

            double marks = Double.parseDouble(marksText);
            if (marks < 0 || marks > 100) {
                throw new IllegalArgumentException("Marks must be between 0 and 100.");
            }

            manager.assignGrade(
                    currentUser.getUsername(),
                    studentUsername,
                    courseName,
                    marks
            );
            teacherGradeStudentField.clear();
            teacherGradeCourseField.clear();
            teacherGradeMarksField.clear();
            refreshTeacherData();
            setStatus("Grade saved.");
        } catch (NumberFormatException ex) {
            setStatus("Grade error: Marks must be a valid number.");
        } catch (Exception ex) {
            setStatus("Grade error: " + ex.getMessage());
        }
    }

    @FXML
    private void onStudentEnrollCourse() {
        try {
            manager.enrollStudentInCourse(currentUser.getUsername(), studentCourseChoice.getValue());
            refreshStudentData();
            setStatus("Course enrollment successful.");
        } catch (Exception ex) {
            setStatus("Enrollment error: " + ex.getMessage());
        }
    }

    @FXML
    private void onLogout() {
        currentUser = null;
        showAuth();
        setStatus("Logged out.");
    }

    private void routeByRole() {
        if (currentUser.getRole() == UserRole.SUPERVISOR) {
            showOnly(supervisorPane);
            refreshSupervisorData();
        } else if (currentUser.getRole() == UserRole.TEACHER) {
            showOnly(teacherPane);
            refreshTeacherData();
        } else {
            showOnly(studentPane);
            refreshStudentData();
        }
    }

    private void refreshSupervisorData() {
        List<Teacher> teachers = manager.getTeachers();
        supervisorTeachersTable.setItems(FXCollections.observableArrayList(teachers));
        supervisorCoursesTable.setItems(FXCollections.observableArrayList(manager.getCourses()));
        supervisorAssignTeacherChoice.setItems(FXCollections.observableArrayList(
                teachers.stream().map(Teacher::getUsername).toList()
        ));
        if (!supervisorAssignTeacherChoice.getItems().isEmpty() && supervisorAssignTeacherChoice.getValue() == null) {
            supervisorAssignTeacherChoice.setValue(supervisorAssignTeacherChoice.getItems().getFirst());
        }
    }

    private void refreshTeacherData() {
        teacherEnrollmentsTable.setItems(FXCollections.observableArrayList(
                manager.getEnrollmentsForTeacher(currentUser.getUsername())
        ));
    }

    private void refreshStudentData() {
        List<Course> courses = manager.getCoursesForStudentSignup();
        studentCourseChoice.setItems(FXCollections.observableArrayList(
                courses.stream().map(Course::getCourseName).toList()
        ));
        if (!studentCourseChoice.getItems().isEmpty() && studentCourseChoice.getValue() == null) {
            studentCourseChoice.setValue(studentCourseChoice.getItems().getFirst());
        }

        studentMyCoursesTable.setItems(FXCollections.observableArrayList(
                manager.getEnrollmentsForStudent(currentUser.getUsername())
        ));
    }

    private void showAuth() {
        showOnly(authPane);
    }

    private void showOnly(Pane pane) {
        authPane.setVisible(false);
        authPane.setManaged(false);
        signupPane.setVisible(false);
        signupPane.setManaged(false);
        supervisorPane.setVisible(false);
        supervisorPane.setManaged(false);
        teacherPane.setVisible(false);
        teacherPane.setManaged(false);
        studentPane.setVisible(false);
        studentPane.setManaged(false);

        pane.setVisible(true);
        pane.setManaged(true);
    }

    private void clearAuthSignup() {
        signupFullNameField.clear();
        signupAgeField.clear();
        signupUsernameField.clear();
        signupPasswordField.clear();
    }

    private void setStatus(String message) {
        StatusMessage statusMessage = new StatusMessage(message);
        Platform.runLater(() -> statusText.setText(statusMessage.text()));
    }
}
