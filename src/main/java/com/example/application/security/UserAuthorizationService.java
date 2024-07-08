package com.example.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.application.data.entity.User;
import com.example.application.data.entity.Course.Course;
import com.example.application.services.DbService;

@Service
public class UserAuthorizationService {
    private final DbService db;

    public UserAuthorizationService(DbService db) {
        this.db = db;
    }

    public boolean isAuthorizedEdit(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Course course = db.getCourse(id);
        User user = db.getUserByUsername(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch((role) -> role.equals("ROLE_ADMIN"));

        return course.getOwner().getId() == user.getId() || isAdmin;
    }

    public boolean isAuthorizedDetails(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String requiredRole = "ROLE_COURSE_" + id;

        boolean hasRole = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch((role) -> role.equals(requiredRole) || role.equals("ROLE_ADMIN"));

        return hasRole;
    }
}