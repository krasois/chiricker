package com.chiricker.areas.home.models.view;

import com.chiricker.areas.home.utils.greeting.GreetingStatus;
import com.chiricker.areas.users.models.view.ProfileViewModel;

public class IndexViewModel {

    private ProfileViewModel profile;

    private GreetingStatus greetingStatus;

    public IndexViewModel() {
    }

    public IndexViewModel(ProfileViewModel profile, GreetingStatus greetingStatus) {
        this.profile = profile;
        this.greetingStatus = greetingStatus;
    }

    public ProfileViewModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileViewModel profile) {
        this.profile = profile;
    }

    public GreetingStatus getGreetingStatus() {
        return greetingStatus;
    }

    public void setGreetingStatus(GreetingStatus greetingStatus) {
        this.greetingStatus = greetingStatus;
    }
}
