package com.example.application.data.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Role extends AbstractEntity {

	@NotEmpty
	private String name;

	@ManyToOne
	@JoinColumn(name = "courseId")
	private Course course;

	@ManyToMany(mappedBy = "roles")
	private List<PlatformUser> users;
}
