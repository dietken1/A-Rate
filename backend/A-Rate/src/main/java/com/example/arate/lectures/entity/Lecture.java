package com.example.arate.lectures.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "professor_id", nullable = false)
    private Long professorId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String department;

    @Column(name = "course_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    @Column(name = "is_english_lecture")
    private Boolean isEnglishLecture = false;

    public enum CourseType {
        교선("1"),
        교필("2"),
        일선("3"),
        전기("4"),
        전선("5"),
        전필("6");

        private final String value;

        CourseType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static CourseType fromValue(String value) {
            for (CourseType type : CourseType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return 교선; // 기본값
        }
    }

    public String getCourseTypeString() {
        return courseType.name();
    }

    public static CourseType getCourseTypeFromString(String courseTypeStr) {
        try {
            return CourseType.valueOf(courseTypeStr);
        } catch (IllegalArgumentException e) {
            return CourseType.교선; // 기본값
        }
    }
} 