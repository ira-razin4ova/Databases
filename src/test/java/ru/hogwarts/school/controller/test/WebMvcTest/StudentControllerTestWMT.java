package ru.hogwarts.school.controller.test.WebMvcTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTestWMT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyRepository facultyRepository;

    @MockitoBean
    private StudentService studentService;

    private List<Student> studentsTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2));

        Student student1 = new Student(1L, "Артём", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", 18, faculty1, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Михаил", 19, null, StudentStatus.ACTIVE);

        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4));
    }

    @Test
    void findByIdStudent() throws Exception {

        Student testStudent = studentsTest.get(0);

        when(studentService.studentSearch(testStudent.getId())).thenReturn(testStudent);

        mockMvc.perform(get("/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    void createStudent() throws Exception {

        Student testStudent = studentsTest.get(0);

        when(studentService.creteStudent(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(post("/student")
                        .content(objectMapper.writeValueAsString(testStudent))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editStudent() throws Exception {

        Student testStudent = studentsTest.get(0);
        testStudent.setName("New Name");
        testStudent.setAge(30);

        when(studentService.editStudent(testStudent)).thenReturn(testStudent);

        mockMvc.perform(put("/student/" + testStudent.getId())
                        .content(objectMapper.writeValueAsString(testStudent)
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testStudent.getName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    void deleteStudent() throws Exception {

        Student testStudent = studentsTest.get(0);

        when(studentService.deleteStudent(testStudent.getId())).thenReturn(testStudent);

        mockMvc.perform(delete("/student/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testStudent.getName()));
    }

    @Test
    void returnBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/student/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findStudentAge() throws Exception {

        int age = 20;

        List<Student> expectedStudent = studentsTest.stream()
                .filter(student -> student.getAge() >= age)
                .collect(Collectors.toUnmodifiableList());

        when(studentService.findByAge(age)).thenReturn(expectedStudent);

        mockMvc.perform(get("/student")
                        .param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }

    @Test
    void findStudentBetweenAge() throws Exception {

        int fromAge = 18;
        int toAge = 20;

        List<Student> expectedStudent = studentsTest.stream()
                .filter(student -> student.getAge() >= fromAge && student.getAge() <= toAge)
                .collect(Collectors.toUnmodifiableList());

        when(studentService.findByAgeBetween(fromAge, toAge)).thenReturn(expectedStudent);

        mockMvc.perform(get("/student/age")
                        .param("from", String.valueOf(fromAge))
                        .param("to", String.valueOf(toAge)))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }
    @Test
    void shouldDownloadStudentsCsv() throws Exception {

        String expectedContent = "id,name,age\n1,Student1,20\n";

        when(studentService.exportStudentToCsv()).thenReturn(expectedContent);

        mockMvc.perform(get("/student/export/csv"))
                .andExpect(status().isOk())

                .andExpect(content().contentType("text/plain")) //тип контента должен быть text/csv
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"students.csv\"")) // заголовки
                .andExpect(result -> {
                    String actualContent = result.getResponse().getContentAsString(); // содержимое
                    assertTrue(actualContent.contains("Student1")); // сравнение содержимого
                });
    }
}
