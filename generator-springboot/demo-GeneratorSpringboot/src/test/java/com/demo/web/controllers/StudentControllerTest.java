package com.demo.web.controllers;

import static com.demo.utils.AppConstants.PROFILE_TEST;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.demo.entities.Student;
import com.demo.services.StudentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@WebMvcTest(controllers = StudentController.class)
@ActiveProfiles(PROFILE_TEST)
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private StudentService studentService;

    @Autowired private ObjectMapper objectMapper;

    private List<Student> studentList;

    @BeforeEach
    void setUp() {
        this.studentList = new ArrayList<>();
        this.studentList.add(new Student(1L, "text 1"));
        this.studentList.add(new Student(2L, "text 2"));
        this.studentList.add(new Student(3L, "text 3"));

        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new ConstraintViolationProblemModule());
    }

    @Test
    void shouldFetchAllStudents() throws Exception {
        given(studentService.findAllStudents()).willReturn(this.studentList);

        this.mockMvc
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(studentList.size())));
    }

    @Test
    void shouldFindStudentById() throws Exception {
        Long studentId = 1L;
        Student student = new Student(studentId, "text 1");
        given(studentService.findStudentById(studentId)).willReturn(Optional.of(student));

        this.mockMvc
                .perform(get("/api/users/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingStudent() throws Exception {
        Long studentId = 1L;
        given(studentService.findStudentById(studentId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/users/{id}", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewStudent() throws Exception {
        given(studentService.saveStudent(any(Student.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        Student student = new Student(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewStudentWithoutText() throws Exception {
        Student student = new Student(null, null);

        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(
                        jsonPath(
                                "$.type",
                                is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        Long studentId = 1L;
        Student student = new Student(studentId, "Updated text");
        given(studentService.findStudentById(studentId)).willReturn(Optional.of(student));
        given(studentService.saveStudent(any(Student.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/users/{id}", student.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingStudent() throws Exception {
        Long studentId = 1L;
        given(studentService.findStudentById(studentId)).willReturn(Optional.empty());
        Student student = new Student(studentId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/users/{id}", studentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        Long studentId = 1L;
        Student student = new Student(studentId, "Some text");
        given(studentService.findStudentById(studentId)).willReturn(Optional.of(student));
        doNothing().when(studentService).deleteStudentById(student.getId());

        this.mockMvc
                .perform(delete("/api/users/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingStudent() throws Exception {
        Long studentId = 1L;
        given(studentService.findStudentById(studentId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/users/{id}", studentId))
                .andExpect(status().isNotFound());
    }
}