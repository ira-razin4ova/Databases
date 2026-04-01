package ru.hogwarts.school.controller.test.WebMvcTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentStatus;
import ru.hogwarts.school.service.FacultyService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;// импортирует сразу все
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;// импортирует сразу все
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTestWMT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

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
    void createFaculty() throws Exception {
        Faculty testFaculty = facultyTest.get(0);

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(testFaculty))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void facultyFindById() throws Exception {
        Faculty testFaculty = facultyTest.get(0);

        when(facultyService.facultySearchId(testFaculty.getId())).thenReturn(testFaculty);

        mockMvc.perform(get("/faculty/" + testFaculty.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Химия"));
    }

    @Test
    void editFaculty() throws Exception {
        Faculty testFaculty = facultyTest.get(0);
        testFaculty.setName("New Name");
        testFaculty.setColor("New Color");

        when(facultyService.editFaculty(testFaculty)).thenReturn(testFaculty);

        mockMvc.perform(put("/faculty/" + testFaculty.getId())
                        .content(objectMapper.writeValueAsString(testFaculty))
                        .contentType(MediaType.APPLICATION_JSON)) // Убрали лишнюю скобку тут!
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.color").value("New Color"));
    }

    @Test
    void deleteFaculty() throws Exception {
        Faculty testFaculty = facultyTest.get(0);

        when(facultyService.deleteFaculty(testFaculty.getId())).thenReturn(testFaculty);

        mockMvc.perform(delete("/faculty/" + testFaculty.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testFaculty.getName()));
    }

    @Test
    void returnBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/faculty/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void FindFacultiesByColor() throws Exception {
        Faculty redFaculty = facultyTest.get(0);

        when(facultyService.findByNameOrColor(null, "Красный"))
                .thenReturn(List.of(redFaculty));

        mockMvc.perform(get("/faculty")
                        .param("color", "Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].color").value("Красный"))
                .andExpect(jsonPath("$[0].name").value("Химия"));
    }

    @Test
    void findFacultiesByName() throws Exception {
        Faculty сhemistryFaculty = facultyTest.get(0);

        when(facultyService.findByNameOrColor("Химия", null))
                .thenReturn(List.of(сhemistryFaculty));

        mockMvc.perform(get("/faculty")
                        .param("name", "Химия"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].color").value("Красный"))
                .andExpect(jsonPath("$[0].name").value("Химия"));
    }

    @ParameterizedTest
    @CsvSource({
            "Физика, ",
            ", Красный",
            "Физика, Красный"
    })
    void shouldFindFacultiesByDifferentColors(String name, String color) throws Exception {
        List<Faculty> expectedFaculties = facultyTest.stream()
                .filter(f -> f.getName().equals(name) || f.getColor().equals(color))
                .toList();
        when(facultyService.findByNameOrColor(name, color))
                .thenReturn(expectedFaculties);

        mockMvc.perform(get("/faculty")
                        .param("name", name)
                        .param("color", color))
                .andExpect(status().isOk());
        // .andExpect(jsonPath("$[0].name").value(name))
        // .andExpect(jsonPath("$[0].color").value(color));
    }

    @Test
    void studentFacultyById() throws Exception{

        Faculty testFaculty = facultyTest.get(0);

        List<Student> expectedStudent = studentsTest.stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().getId().equals(testFaculty.getId()))
                .toList();
        when(facultyService.studentsFacultyById(testFaculty.getId())).thenReturn(expectedStudent);

        mockMvc.perform(get("/faculty/"+ testFaculty.getId() + "/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedStudent.size()))
                .andExpect(jsonPath("$[0].name").exists());
    }

}
