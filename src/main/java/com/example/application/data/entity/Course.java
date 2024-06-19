package com.example.application.data.entity;

import java.util.List;

import com.example.application.data.entity.CourseContentClasses.Panel;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
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
	private String shortName;

	@Getter
	@Setter
	private String key = null;

	@Getter
	@Setter
	private String bannerUrl = null;

	@Getter
	@Setter
	@OneToOne
	@NotEmpty
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
