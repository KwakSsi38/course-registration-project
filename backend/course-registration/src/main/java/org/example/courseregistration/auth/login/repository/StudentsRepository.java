package org.example.courseregistration.auth.login.repository;

import org.example.courseregistration.entity.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<Students, String> {
    Optional<Students> findById(String id);
}
