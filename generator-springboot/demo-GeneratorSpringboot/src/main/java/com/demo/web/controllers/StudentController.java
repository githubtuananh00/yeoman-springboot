package com.demo.web.controllers;

import com.demo.entities.Student;
import com.demo.entities.ResponseObj;
import com.demo.services.StudentService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/users")
@Slf4j
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<ResponseObj> getAllStudents() {
                ;
        return ResponseEntity.status(200).body(
            new ResponseObj(true, "Get all students successfully",studentService.findAllStudents())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObj> getStudentById(@PathVariable Long id) {
        
        
        
        return ResponseEntity.status(200).body(new ResponseObj(true,
        "Get a student successfully",
        studentService
                .findStudentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build())));
    }

    @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseObj> createStudent(@RequestBody @Validated Student student) {
        return ResponseEntity.status(200).body(new ResponseObj(
            true,
            "Create a Student successfully",
            studentService.saveStudent(student)
        )); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObj> updateStudent(
            @PathVariable Long id, @RequestBody Student student) {
        
        
                return ResponseEntity.status(200).body(new ResponseObj(
                    true,
                    "Update a Student successfully",
                    studentService
                    .findStudentById(id)
                    .map(
                            studentObj -> {
                                student.setId(id);
                                return ResponseEntity.ok(studentService.saveStudent(student));
                            })
                    .orElseGet(() -> ResponseEntity.notFound().build())
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObj> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.status(200).body(new ResponseObj(
            true,
            "Delete a Student successfully",
            studentService
                .findStudentById(id)
                .map(
                        student -> {
                            studentService.deleteStudentById(id);
                            return ResponseEntity.ok(student);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build())
        ));
        
        
    }
}