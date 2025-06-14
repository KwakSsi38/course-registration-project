package org.example.courseregistration.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Professors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Professors {
    @Id
    @Column(name = "p_id", length = 10, nullable = false)
    private String id;

    @Column(name = "p_name", length = 30, nullable = false)
    private String name;

    @Column(name = "p_major", length = 30)
    private String major;
}

