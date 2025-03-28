package com.george.mdtrack.repository;

import com.george.mdtrack.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

    UserProfile findByUserId(Long userId);
}
