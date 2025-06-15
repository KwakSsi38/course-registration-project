package org.example.courseregistration.sub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Courses {
    @Id
    @Column(name = "c_id_no", length = 10, nullable = false)
    private String idNo;

    @Column(name = "c_id", length = 50, nullable = false)
    private String name;

    @Column(name = "c_unit", nullable = false)
    private Integer unit;

    @Column(name = "c_major", length = 30, nullable = false)
    private String major;
}

