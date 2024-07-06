package com.example.application.data.entity.CourseContentClasses;

import com.example.application.data.entity.AbstractEntity;
import com.example.application.data.entity.Course;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Panel extends AbstractEntity {
	@Getter
	@Setter
	private String title;

	@Getter
	@Setter
	private String content;

	@ManyToOne
	private Course course;
}
