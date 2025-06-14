package org.example.courseregistration.mypage.repository;

import org.example.courseregistration.entity.Enroll;
import org.example.courseregistration.mypage.dto.TimeTableResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enroll, Long> {

    @Query("""
    SELECT new org.example.courseregistration.mypage.dto.TimeTableResponse(
        e.course.idNo,               
        e.course.name,               
        e.section,                   
        e.score,                     
        e.course.major,              
        s.professor.name,            
        s.time,                      
        s.classroom                  
    )
    FROM Enroll e
    JOIN Sections s ON 
    s.course.idNo = e.course.idNo AND 
    s.section = e.section AND
    s.year = e.year AND
    s.semester = e.semester 
    WHERE e.student.id = :studentId AND e.year = :year AND e.semester = :semester
""")
    List<TimeTableResponse> findEnrollmentDetails(
            @Param("studentId") String studentId,
            @Param("year") int year,
            @Param("semester") int semester
    );
}
