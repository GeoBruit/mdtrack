package com.george.mdtrack.service;

import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.enums.UserRoles;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.UserRepo;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class UserService {

    private final static Logger logger = Logger.getLogger(UserService.class.getName());
    //PasswordEncoder to encode all the passwords before we are saving them to the database
    private final BCryptPasswordEncoder passwordEncoder;
    //This is how we access the database
    private final UserRepo userRepo;


    //Spring boot injects the dependency here
    public UserService(UserRepo userRepo) {

        this.userRepo = userRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();

    }


    /**
     * This function takes an UserDTO object and converts
     * it to a User entity class before saving it to the DB
     *
     * @param userRegisterDTO the UserDTO send from the front end
     */
    public User saveUser(UserRegisterDTO userRegisterDTO) {
        try {

            User userToBeSaved = parseUserDTOToUserEntity(userRegisterDTO);

            return userRepo.save(userToBeSaved);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("UserRegisterDTO is null or has null values");

        } catch (DataAccessException e) {
            throw new RuntimeException("Database exception");

        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown exception", e);
        }
    }

    private User parseUserDTOToUserEntity(UserRegisterDTO userRegisterDTO) {

        if (userRegisterDTO == null || userRegisterDTO.getUsername() == null || userRegisterDTO.getEmail() == null) {
            throw new IllegalArgumentException("UserRegisterDTO is null or has null values");

        } else {
            //create new empty user
            User userToBeSaved = new User();
            //Set all the User Attributes
            userToBeSaved.setUsername(userRegisterDTO.getUsername());
            userToBeSaved.setEmail(userRegisterDTO.getEmail());
            //Encoding the password
            String hashedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
            userToBeSaved.setPassword(hashedPassword);
            userToBeSaved.setUserRole(UserRoles.USER_ROLE.toString());
            return userToBeSaved;
        }
    }
}
