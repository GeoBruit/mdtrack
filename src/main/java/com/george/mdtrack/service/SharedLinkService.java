    package com.george.mdtrack.service;

    import com.george.mdtrack.model.SharedLink;
    import com.george.mdtrack.model.User;
    import com.george.mdtrack.repository.SharedLinkRepo;

    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.util.Arrays;
    import java.util.List;

    /*
     * Service class responsible for handling logic related to Shared Links.
     * It communicates with the SharedLinkRepo to store and retrieve links,
     * and with UserService to retrieve user information.
     */

    @Service
    public class SharedLinkService {

        private SharedLinkRepo sharedLinkRepo;
        private UserService userService;
        private final String sharedLinkUrlBase = "/user/shared/";

        /*
         * Constructor for injecting dependencies.
         * @param sharedLinkRepo  repository for shared links
         * @param userService     service to fetch user data
         */
        public SharedLinkService(SharedLinkRepo sharedLinkRepo, UserService userService) {
            this.sharedLinkRepo = sharedLinkRepo;
            this.userService = userService;
        }


        /*
         * Retrieves all shared links for a specific user, ordered by most recent timestamp.
         * @param userId  ID of the user whose links are being retrieved
         * @return a list of SharedLink objects
         */
        public List<SharedLink> getSharedLinks(Long userId) {

            try{
                return sharedLinkRepo.getByUserIdOrderByTimeStampDesc(userId);
            }catch (Exception e){

                throw new RuntimeException("Error getting shared links");
            }

        }
        /*
         * Creates a new shared link for a user and saves it to the repository.
         * The link includes the user ID and timestamp in the URL.
         * @param userId  ID of the user creating the link
         * @return the sharedLinkRepo instance (note: this likely should return the SharedLink instead)
         */
        public SharedLinkRepo createSharedLink(Long userId) {

            try{
                User userCreatingTheLink = userService.getUserByUserId(userId);
                SharedLink sharedLink = new SharedLink();
                sharedLink.setUser(userCreatingTheLink);
                //Setting the data and time the link was created
                sharedLink.setTimeStamp(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                sharedLink.setLink(sharedLinkUrlBase + userCreatingTheLink.getId().toString() + "/" + sharedLink.getTimeStamp());
                sharedLinkRepo.save(sharedLink);

                return sharedLinkRepo;
            }catch (Exception e){

                e.printStackTrace();
                return null;
            }
        }
    }
