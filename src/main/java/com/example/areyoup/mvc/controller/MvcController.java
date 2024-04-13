package com.example.areyoup.mvc.controller;

import com.example.areyoup.mvc.service.MvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mvc")
public class MvcController {

    @Autowired
    MvcService mvcService;

    @GetMapping("/profile")
    public String profileAll(Model model){
        model.addAttribute("profiles", mvcService.getProfile());
        return "profile";
    }

    @GetMapping("/")
    public String index(){
        return "index";
    }

}
