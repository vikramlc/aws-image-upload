package com.vikram.awsimageupload.datastore;

import com.vikram.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
//        USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "Ajay", null));
//        USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "Vijay", null));
//        USER_PROFILES.add(new UserProfile(UUID.randomUUID(), "Vikram", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("3456dc4c-912e-4055-9a8c-2f1d3a525e90"),
                "Ajay", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("28ffdc1d-4aab-4522-b1fe-56e7a8bcd7a7"),
                "Vijay", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("1c14b080-b129-4111-9c17-abb47c2985c9"),
                "Vikram", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
