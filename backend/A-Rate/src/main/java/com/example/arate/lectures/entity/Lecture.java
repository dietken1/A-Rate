package com.example.arate.lectures.entity;

import com.example.arate.professors.entity.Professor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", referencedColumnName = "id", nullable = false)
    private Professor professor;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String department;

    @Column(name = "course_type", nullable = false)
    @Convert(converter = CourseTypeConverter.class)
    private CourseType courseType;

    @Column(name = "is_english_lecture")
    private Boolean isEnglishLecture = false;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureEvaluation> evaluations = new ArrayList<>();

    public enum CourseType {
        // DB에 저장된 값과 일치하도록 수정
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

    @Converter
    public static class CourseTypeConverter implements AttributeConverter<CourseType, String> {
        @Override
        public String convertToDatabaseColumn(CourseType courseType) {
            return courseType != null ? courseType.getValue() : null;
        }

        @Override
        public CourseType convertToEntityAttribute(String dbData) {
            return dbData != null ? CourseType.fromValue(dbData) : null;
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