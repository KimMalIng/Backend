package com.example.areyoup.profile.controller;

import com.example.areyoup.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @GetMapping("/test/profile")
    public String profileAll(Model model){
        model.addAttribute("profiles", profileService.getProfile());
        return "profile";
    }

}
