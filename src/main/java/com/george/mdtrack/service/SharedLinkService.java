package com.george.mdtrack.service;

import com.george.mdtrack.model.SharedLink;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.SharedLinkRepo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SharedLinkService {

    private SharedLinkRepo sharedLinkRepo;
    private UserService userService;
    private final String sharedLinkUrlBase = "/user/shared/";

    public SharedLinkService(SharedLinkRepo sharedLinkRepo, UserService userService) {
        this.sharedLinkRepo = sharedLinkRepo;
        this.userService = userService;
    }


    public SharedLinkRepo createSharedLink(Long userId) {

        try{
            User userCreatingTheLink = userService.getUserByUserId(userId);
            SharedLink sharedLink = new SharedLink();
            sharedLink.setUser(userCreatingTheLink);
            //Setting the data and time the link was created
            sharedLink.setTimestamp(LocalDateTime.now());
            sharedLink.setLink(sharedLinkUrlBase + userCreatingTheLink.getId().toString() + "/" + sharedLink.getTimestamp());
            sharedLinkRepo.save(sharedLink);

            return sharedLinkRepo;
        }catch (Exception e){

            e.printStackTrace();
            return null;
        }
    }
}
