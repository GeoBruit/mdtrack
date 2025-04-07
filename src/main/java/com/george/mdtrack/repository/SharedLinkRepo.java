package com.george.mdtrack.repository;

import com.george.mdtrack.model.SharedLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedLinkRepo extends JpaRepository<SharedLink, Long> {

    List<SharedLink> getByUserIdOrderByTimeStampDesc(Long UserId);
}
