package org.example.courseregistration.course.enroll.repository;

import org.example.courseregistration.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollStudentsRepository extends JpaRepository<Students, String> {
}