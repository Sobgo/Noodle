package com.example.application.data.entity.CourseClasses;

import com.example.application.data.entity.AbstractEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Panel extends AbstractEntity {
	@Getter
	@Setter
	private String title = "";

	@Getter
	@Setter
	private String content = "";

	@Getter
	@Setter
	@ManyToOne
	private Course course;

	@Getter
	@Setter
	private Integer orderIndex = 0;

	public Panel() {}

	public Panel(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
}
