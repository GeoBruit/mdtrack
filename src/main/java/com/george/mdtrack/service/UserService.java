package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.enums.UserRoles;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.model.UserProfile;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserProfileRepo;
import com.george.mdtrack.repository.UserRepo;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService implements UserDetailsService {

    private final static Logger logger = Logger.getLogger(UserService.class.getName());
    //PasswordEncoder to encode all the passwords before we are saving them to the database
    private final BCryptPasswordEncoder passwordEncoder;
    //This is how we access the database
    private final UserRepo userRepo;
    private final MedicalNoteRepo medicalNoteRepo;
    private final UserProfileRepo  userProfileRepo;


    //Spring boot injects the dependency here
    public UserService(UserRepo userRepo, MedicalNoteRepo medicalNoteRepo, UserProfileRepo userProfileRepo) {

        this.userRepo = userRepo;
        this.medicalNoteRepo = medicalNoteRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userProfileRepo = userProfileRepo;

    }


    public UserProfile getUserProfileByUserId(Long userId){

        UserProfile userProfile = userProfileRepo.findByUserId(userId);
        if(userProfile == null){
            return new UserProfile();
        }
        return userProfile;
    }

    /**
     * Saves the user profile for a specific user.
     *
     * @param userProfile the user profile object to be saved
     * @param userId the unique identifier of the user for whom the profile is being saved
     * @throws IllegalArgumentException if the user with the provided userId is not found
     * @throws RuntimeException if there is an error saving the user profile to the database
     */
    public void saveUserProfile(UserProfile userProfile, Long userId){

        try{
            //User for whom we re saving the profile
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
            //Setting user profile for user and user for userProfile
            user.setUserProfile(userProfile);
            userProfile.setUser(user);
            //Saving the profile to the db
            userRepo.save(user);

        }catch (Exception e){
            System.out.println(e.getMessage());

            throw new RuntimeException("Error saving user profile to database");
        }

    }


    /**
     * Retrieves a list of medical notes associated with a user, based on their user role.
     * If the user is a doctor, retrieves the medical notes authored by the doctor.
     * If the user is a patient, retrieves the medical notes associated with the patient.
     *
     * @param userId the unique identifier of the user
     * @return a list of medical notes related to the user
     * @throws IllegalArgumentException if the user is not found or if no medical notes are available for the user
     */
    public List<MedicalNote> getMedicalNotesByUserId(Long userId){

        //Declaring an empty list of medical notes
        List<MedicalNote> medicalNoteToBeReturned = List.of();
        
        //Check if user is patient or doctor
        User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        if (user.getUserRole().contains("DOCTOR")){
            medicalNoteToBeReturned = medicalNoteRepo.findByDoctorId(userId);
        } else if ( user.getUserRole().contains("PATIENT")) {
            //Fetching all the medical notes that reference the patient id
            medicalNoteToBeReturned = medicalNoteRepo.findByPatientId(userId);
        }
        //checking if the medical note is empty
        if(medicalNoteToBeReturned.isEmpty()){
            //we are returning an empty list
            return Collections.emptyList();
        }
        
        return medicalNoteToBeReturned;
    }


    public User getUserByUsername(String username){
        try {
            return userRepo.findByUsername(username);
        }catch (Exception e){
            throw new IllegalArgumentException("User with username " + username + " not found");
        }
    }


    /**
     * Retrieves a User based on the provided userId.
     *
     * @param userId the unique identifier of the user to be retrieved
     * @return the User object that matches the given userId
     * @throws IllegalArgumentException if no user is found with the specified userId
     */
    public User getUserByUserId(Long userId) throws IllegalArgumentException{
        return userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
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
            throw new RuntimeException("Database exception", e.getCause());

        } catch (RuntimeException e) {
            throw new RuntimeException("Unknown exception", e);
        }
    }

    /**
     * Parses a UserRegisterDTO object into a User entity by transferring and transforming relevant fields.
     * Ensures that the input object and its fields are neither null nor empty.
     *
     * @param userRegisterDTO the data transfer object containing user registration details
     * @return a User entity with attributes set based on the provided UserRegisterDTO
     * @throws IllegalArgumentException if userRegisterDTO is null, contains null values, or contains empty values
     */
    private User parseUserDTOToUserEntity(UserRegisterDTO userRegisterDTO) {

        //Making sure we don't have null values
        if (userRegisterDTO == null || userRegisterDTO.getUsername() == null || userRegisterDTO.getEmail() == null) {
            throw new IllegalArgumentException("UserRegisterDTO is null or has null values");

            //checking for empty string values
        } else if (userRegisterDTO.getUsername().isEmpty() || userRegisterDTO.getEmail().isEmpty() || userRegisterDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("UserRegisterDTO has empty values");
        } else {
            //create new empty user
            User userToBeSaved = new User();
            //Set all the User Attributes
            userToBeSaved.setUsername(userRegisterDTO.getUsername());
            userToBeSaved.setEmail(userRegisterDTO.getEmail().toLowerCase());//setting the email to lower case
            //Encoding the password
            userToBeSaved.setPassword(hashPassword(userRegisterDTO.getPassword()));
            userToBeSaved.setUserRole(assignUserRole());
            return userToBeSaved;
        }
    }

    //This function returns userRole as a string
    //TODO this will have to be changed for a better approach
    private String assignUserRole(){

        return UserRoles.USER_ROLE.toString() + "," + UserRoles.PATIENT.toString();
    }

    /**
     * Hashes the given plain text password using a password encoder.
     *
     * @param password the plain text password to be hashed
     * @return the hashed version of the password
     */
    private String hashPassword(String password){
        return passwordEncoder.encode(password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }
}
