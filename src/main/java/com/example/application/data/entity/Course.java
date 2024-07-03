package com.example.application.data.entity;

import java.util.List;

import com.example.application.data.entity.CourseContentClasses.Panel;

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
	@OneToMany(mappedBy = "course")
	private List<Panel> panels;

	public void addPanel(Panel p) {
		panels.add(p);
	}

	public void removePanel(int idx) {
		panels.remove(idx);
	}
}
