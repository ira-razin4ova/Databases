package ru.hogwarts.school.controller.test.WebMvcTest;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.AvatarController;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

@WebMvcTest
public class AvatarControllerTestWMT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvatarRepository avatarRepository;

    @MockitoBean
    private StudentService studentService;

    @MockitoSpyBean
    private AvatarService avatarService;

    @InjectMocks
    private AvatarController avatarController;
}
