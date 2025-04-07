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

@Service
public class SharedLinkService {

    private SharedLinkRepo sharedLinkRepo;
    private UserService userService;
    private final String sharedLinkUrlBase = "/user/shared/";

    public SharedLinkService(SharedLinkRepo sharedLinkRepo, UserService userService) {
        this.sharedLinkRepo = sharedLinkRepo;
        this.userService = userService;
    }



    public List<SharedLink> getSharedLinks(Long userId) {

        try{
            return sharedLinkRepo.getByUserIdOrderByTimeStampDesc(userId);
        }catch (Exception e){

            throw new RuntimeException("Error getting shared links");
        }

    }

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
