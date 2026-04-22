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
import ru.hogwarts.school.dto.avatar.AvatarDto;
import ru.hogwarts.school.dto.faculty.CreateFacultyDto;
import ru.hogwarts.school.dto.faculty.FacultyDto;
import ru.hogwarts.school.dto.student.CreateStudentDto;
import ru.hogwarts.school.dto.student.StudentDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.constant.StudentStatus;
import ru.hogwarts.school.service.FacultyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
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

    @MockitoBean
    private FacultyDto facultyDto;

    private List<Student> studentsTest;
    private List<StudentDto> studentDtosTest;
    private List<Faculty> facultyTest;
    private List<FacultyDto> facultyDtoList;

    @BeforeEach
    void setUp() {
        Faculty faculty1 = new Faculty(1L, "Химия", "Красный");
        Faculty faculty2 = new Faculty(2L, "Физика", "Синий");

        facultyTest = new ArrayList<>(List.of(faculty1, faculty2));

        facultyDtoList = List.of(
                new FacultyDto(1L, "Химия", "Красный", new BigDecimal("1000.00")),
                new FacultyDto(2L, "Физика", "Синий", new BigDecimal("2500.50")),
                new FacultyDto(3L, "Биология", "Зеленый", new BigDecimal("0.00")),
                new FacultyDto(4L, "Математика", "Белый", new BigDecimal("50000.00"))
        );

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
    void createFaculty() throws Exception {
        CreateFacultyDto createFacultyDto = new CreateFacultyDto("Химия", "Красный");

        FacultyDto dto = new FacultyDto(1L, "Химия", "Красный", new BigDecimal("1000.00"));

        when(facultyService.createFaculty(any(CreateFacultyDto.class))).thenReturn(dto);

        mockMvc.perform(post("/faculties")
                        .content(objectMapper.writeValueAsString(createFacultyDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void facultyFindById() throws Exception {
        Faculty testFaculty = facultyTest.get(0);
        FacultyDto dto = new FacultyDto(1L, "Химия", "Красный", new BigDecimal("1000.00"));

        when(facultyService.getById(testFaculty.getId())).thenReturn(dto);

        mockMvc.perform(get("/faculties/" + testFaculty.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Химия"));
    }

    @Test
    void editFaculty() throws Exception {
        Faculty testFaculty = facultyTest.get(0);
        testFaculty.setName("New Name");
        testFaculty.setColor("New Color");

        when(facultyService.editFaculty(testFaculty)).thenReturn(testFaculty);

        mockMvc.perform(put("/faculties/" + testFaculty.getId())
                        .content(objectMapper.writeValueAsString(testFaculty))
                        .contentType(MediaType.APPLICATION_JSON)) // Убрали лишнюю скобку тут!
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.color").value("New Color"));
    }

    @Test
    void deleteFaculty() throws Exception {
        Faculty testFaculty = facultyTest.get(0);
        Long id = testFaculty.getId();

        doNothing().when(facultyService).deleteFaculty(id);

        mockMvc.perform(delete("/faculties/" + testFaculty.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Факультет с ID " + id + " успешно удален"));
    }

    @Test
    void returnBadRequestWhenIdIsNegative() throws Exception {
        mockMvc.perform(get("/faculties/-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void FindFacultiesByColor() throws Exception {
        FacultyDto redFaculty = facultyDtoList.get(0);

        when(facultyService.findByNameOrColor(null, "Красный"))
                .thenReturn(List.of(redFaculty));

        mockMvc.perform(get("/faculties")
                        .param("color", "Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].color").value("Красный"))
                .andExpect(jsonPath("$[0].name").value("Химия"));
    }

    @Test
    void findFacultiesByName() throws Exception {
        FacultyDto сhemistryFaculty = facultyDtoList.get(0);

        when(facultyService.findByNameOrColor("Химия", null))
                .thenReturn(List.of(сhemistryFaculty));

        mockMvc.perform(get("/faculties")
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
        List<FacultyDto> facultyDtoList = List.of(
                new FacultyDto(1L, "Химия", "Красный", new BigDecimal("1000.00")),
                new FacultyDto(2L, "Физика", "Синий", new BigDecimal("2500.50")),
                new FacultyDto(3L, "Биология", "Зеленый", new BigDecimal("0.00")),
                new FacultyDto(4L, "Математика", "Белый", new BigDecimal("50000.00"))
        );
        List<FacultyDto> expectedFaculties = facultyDtoList.stream()
                .filter(f -> f.name().equals(name) || f.color().equals(color))
                .toList();
        when(facultyService.findByNameOrColor(name, color))
                .thenReturn(expectedFaculties);

        mockMvc.perform(get("/faculties")
                        .param("name", name)
                        .param("color", color))
                .andExpect(status().isOk());
    }

    @Test
    void studentFacultyById() throws Exception {

        Faculty testFaculty = facultyTest.get(0);

        List<StudentDto> expectedStudent = studentDtosTest.stream()
                .filter(s -> s.getFaculty() != null && s.getFaculty().equals(testFaculty.getId()))
                .toList();
        when(facultyService.studentsFacultyById(testFaculty.getId())).thenReturn(expectedStudent);

        mockMvc.perform(get("/faculties/" + testFaculty.getId() + "/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedStudent.size()));
    }

}
