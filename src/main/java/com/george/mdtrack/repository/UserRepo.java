package com.george.mdtrack.repository;

import com.george.mdtrack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email);

    @Query("""
    SELECT u FROM User u
    JOIN u.userProfile up
    WHERE LOWER(up.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(up.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<User> searchByUserProfileName(@Param("query") String query);


    // Matches either first or last name (for single word searches)
    @Query("""
    SELECT u FROM User u
    JOIN u.userProfile up
    WHERE LOWER(up.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(up.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<User> searchByFirstOrLastName(@Param("query") String query);


    // Matches both first and last name (for two-word searches)
    @Query("""
    SELECT u FROM User u
    JOIN u.userProfile up
    WHERE LOWER(up.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))
      AND LOWER(up.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))
""")
    List<User> searchByFirstAndLastName(@Param("firstName") String firstName,
                                        @Param("lastName") String lastName);

}
