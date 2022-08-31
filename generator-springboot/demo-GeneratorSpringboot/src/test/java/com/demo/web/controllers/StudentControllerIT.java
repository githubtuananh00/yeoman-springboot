package com.demo.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demo.common.AbstractIntegrationTest;
import com.demo.entities.Student;
import com.demo.repositories.StudentRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class StudentControllerIT extends AbstractIntegrationTest {

    @Autowired private StudentRepository studentRepository;

    private List<Student> studentList = null;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        studentList = new ArrayList<>();
        studentList.add(new Student(1L, "First Student"));
        studentList.add(new Student(2L, "Second Student"));
        studentList.add(new Student(3L, "Third Student"));
        studentList = studentRepository.saveAll(studentList);
    }

    @Test
    void shouldFetchAllStudents() throws Exception {
        this.mockMvc
                .perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(studentList.size())));
    }

    @Test
    void shouldFindStudentById() throws Exception {
        Student student = studentList.get(0);
        Long studentId = student.getId();

        this.mockMvc
                .perform(get("/api/users/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldCreateNewStudent() throws Exception {
        Student student = new Student(null, "New Student");
        this.mockMvc
                .perform(
                        post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
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
        Student student = studentList.get(0);
        student.setText("Updated Student");

        this.mockMvc
                .perform(
                        put("/api/users/{id}", student.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        Student student = studentList.get(0);

        this.mockMvc
                .perform(delete("/api/users/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(student.getText())));
    }
}