package org.example.courseregistration.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "Sections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_sections_unique",
                        columnNames = {"c_id_no", "se_year", "se_semester", "se_section"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sections {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_id_no", nullable = false)
    private Courses course;

    @Column(name = "c_id", length = 50, nullable = false)
    private String courseName;

    @Column(name = "se_year", nullable = false)
    private Integer year;

    @Column(name = "se_semester", nullable = false)
    private Integer semester;

    @Column(name = "se_section", nullable = false)
    private Integer section;

    @Column(name = "se_time", length = 20, nullable = false)
    private String time;

    @Column(name = "se_capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "p_id", nullable = false)
    private Professors professor;

    @Column(name = "se_classroom", length = 30)
    private String classroom;
}