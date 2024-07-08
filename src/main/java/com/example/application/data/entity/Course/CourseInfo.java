package com.example.application.data.entity.Course;

import com.example.application.data.entity.Role;

public interface CourseInfo {
	Long getId();
	String getName();
	String getKey();
	Role getCourseRole();
	byte[] getBanner();
}
