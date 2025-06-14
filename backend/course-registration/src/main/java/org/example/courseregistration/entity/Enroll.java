package org.example.courseregistration.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Enroll")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Enroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "s_id", nullable = false)
    private Students student;

    @Column(name = "e_year", nullable = false)
    private Integer year;

    @Column(name = "e_semester", nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id_no", nullable = false)
    private Courses course;

    @Column(name = "e_section", nullable = false)
    private Integer section;

    @Column(name = "c_id", length = 50, nullable = false)
    private String courseName;

    @Column(name = "e_score")
    private Float score;
}

