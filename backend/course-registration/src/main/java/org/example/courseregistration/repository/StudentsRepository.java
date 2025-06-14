package org.example.courseregistration.repository;

import org.example.courseregistration.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentsRepository extends JpaRepository<Students, String> {
}