package org.example.courseregistration.repository;

import org.example.courseregistration.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollStudentsRepository extends JpaRepository<Students, String> {
}