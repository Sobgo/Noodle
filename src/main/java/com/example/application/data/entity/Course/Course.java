package com.example.application.data.entity.Course;

import java.util.ArrayList;
import java.util.List;

import com.example.application.data.entity.AbstractEntity;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Course extends AbstractEntity {
	@Getter
	@Setter
	@NotEmpty
	private String name;

	@Getter
	@Setter
	private String key = null;

	@Getter
	@Setter
	@NotNull
	private boolean visible;

	@Getter
	@Setter
	@Lob
	@Column(length = 10 * 1000 * 1000)
	private byte[] banner = null;

	@Getter
	@Setter
	@ManyToOne
	@NotNull
	private User owner;

	@Getter
	@Setter
	@OneToOne
	@NotNull
	private Role courseRole;

	@Getter
	@Setter
	@OneToMany(mappedBy = "course")
	private List<Panel> panels = new ArrayList<>();
}
