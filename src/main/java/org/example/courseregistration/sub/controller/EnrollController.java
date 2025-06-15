// ✅ 경로: src/main/java/org/example/courseregistration/controller/EnrollController.java

package org.example.courseregistration.sub.controller;

import lombok.RequiredArgsConstructor;
import org.example.courseregistration.sub.dto.DeleteRequestDto;
import org.example.courseregistration.sub.dto.EnrollRequestDto;
import org.example.courseregistration.sub.entity.Students;
import org.example.courseregistration.sub.repository.EnrollStudentsRepository;
import org.example.courseregistration.sub.service.EnrollService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class EnrollController {

    private final EnrollService enrollService;
    private final EnrollStudentsRepository studentsRepository;

    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody EnrollRequestDto request) {
        Students student = studentsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("해당 학생을 찾을 수 없습니다."));
        if (!"재학".equals(student.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ 재학생만 수강신청이 가능합니다.");
        }

        String result = enrollService.apply(request.getStudentId(), request.getSectionId());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody DeleteRequestDto request) {
        Students student = studentsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("해당 학생을 찾을 수 없습니다."));
        if (!"재학".equals(student.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ 재학생만 수강취소가 가능합니다.");
        }

        String result = enrollService.cancel(request.getStudentId(), request.getCourseIdNo(), request.getSectionNo());
        return ResponseEntity.ok(result);
    }
}