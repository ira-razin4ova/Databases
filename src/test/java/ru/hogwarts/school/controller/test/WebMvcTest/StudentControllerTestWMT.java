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
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.DataCodecService;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private DataCodecService dataCodecService;

    @MockitoBean
    private StudentMapper studentMapper;

    @MockitoBean
    private FacultyService facultyService;

    private List<Student> studentsTest;
    private List<StudentDto> studentDtosTest;
    private List<Faculty> facultyTest;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2));

        Student student1 = new Student(1L, "Артём", "Смирнов", 23, faculty1, StudentStatus.ACTIVE);
        Student student2 = new Student(2L, "Мария", "Зайцева", 20, faculty1, StudentStatus.ACTIVE);
        Student student3 = new Student(3L, "Марат", "Афонин", 18, faculty1, StudentStatus.ACTIVE);
        Student student4 = new Student(4L, "Михаил", "Башаров", 19, null, StudentStatus.ACTIVE);
        studentsTest = new ArrayList<>(List.of(student1, student2, student3, student4));

        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", student1.getId());

        StudentDto sDto1 = new StudentDto(1L, 23, "Артём", "Смирнов", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160678", "123-456");
        StudentDto sDto2 = new StudentDto(2L, 20, "Мария", "Леонова", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160679", "123-457");
        StudentDto sDto3 = new StudentDto(3L, 18, "Марат", "Измалков", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160680", "123-458");
        StudentDto sDto4 = new StudentDto(4L, 18, "Софья", "Афонина", "Химия", avatarDto, StudentStatus.ACTIVE, "79536160681", "123-459");
        StudentDto sDto5 = new StudentDto(5L, 19, "Михаил", "Бачурин", null, avatarDto, StudentStatus.ACTIVE, "79536160682", "123-460");

        studentDtosTest = new ArrayList<>(List.of(sDto1, sDto2, sDto3, sDto4, sDto5));
    }

    @Test
    void findByIdStudent() throws Exception {

        Student testStudent = studentsTest.get(0);

        when(studentService.getStudentOrThrow(testStudent.getId())).thenReturn(testStudent);

        mockMvc.perform(get("/students/" + testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testStudent.getFirstName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    void createStudent() throws Exception {

        CreateStudentDto cDto1 = new CreateStudentDto(23, 1L, "Артём", "Смирнов", "79536160678", StudentStatus.ACTIVE, "123-456");
        AvatarDto avatarDto = new AvatarDto(null, "fail.path", "path.preview", 1L);
        StudentDto sDto1 = new StudentDto(1L, 23, "Артём", "Смирнов", "Химия",avatarDto , StudentStatus.ACTIVE, "79536160678", "123-456");

        when(studentService.createStudent(any(CreateStudentDto.class))).thenReturn(sDto1);

        mockMvc.perform(post("/students")
                        .content(objectMapper.writeValueAsString(cDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void editStudent() throws Exception {

        Student testStudent = studentsTest.get(0);
        testStudent.setFirstName("New Name");
        testStudent.setAge(30);

        when(studentService.editStudent(testStudent)).thenReturn(testStudent);

        mockMvc.perform(put("/students/" + testStudent.getId())
                        .content(objectMapper.writeValueAsString(testStudent)
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testStudent.getFirstName()))
                .andExpect(jsonPath("$.age").value(testStudent.getAge()));
    }

    @Test
    void deleteStudent() throws Exception {

        Student testStudent = studentsTest.get(0);

        doNothing().when(studentService).deleteStudent(testStudent.getId());

        mockMvc.perform(delete("/students/" + testStudent.getId()))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(testStudent.getId());
    }

    @Test
    void returnBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/students/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findStudentAge() throws Exception {

        int age = 20;

        List<StudentDto> expectedStudent = studentDtosTest.stream()
                .filter(student -> student.getAge() >= age)
                .collect(Collectors.toUnmodifiableList());

        when(studentService.findByAge(age)).thenReturn(expectedStudent);

        mockMvc.perform(get("/students")
                        .param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }

    @Test
    void findStudentBetweenAge() throws Exception {

        int fromAge = 18;
        int toAge = 20;

        List<StudentDto> expectedStudent = studentDtosTest.stream()
                .filter(student -> student.getAge() >= fromAge && student.getAge() <= toAge)
                .collect(Collectors.toUnmodifiableList());

        when(studentService.findByAgeBetween(fromAge, toAge)).thenReturn(expectedStudent);

        mockMvc.perform(get("/students/age")
                        .param("from", String.valueOf(fromAge))
                        .param("to", String.valueOf(toAge)))
                .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }
    @Test
    void shouldDownloadStudentsCsv() throws Exception {

        String expectedContent = "id,name,age\n1,Student1,20\n";

        when(studentService.exportStudentToCsv()).thenReturn(expectedContent);

        mockMvc.perform(get("/students/export/csv"))
                .andExpect(status().isOk())

                .andExpect(content().contentType("text/plain")) //тип контента должен быть text/csv
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"students.csv\"")) // заголовки
                .andExpect(result -> {
                    String actualContent = result.getResponse().getContentAsString(); // содержимое
                    assertTrue(actualContent.contains("Student1")); // сравнение содержимого
                });
    }
}
