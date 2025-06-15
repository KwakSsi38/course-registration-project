package org.example.courseregistration.sub.repository;

import org.example.courseregistration.sub.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollStudentsRepository extends JpaRepository<Students, String> {
}