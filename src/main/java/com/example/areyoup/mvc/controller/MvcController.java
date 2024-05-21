package com.example.areyoup.mvc.controller;

import com.example.areyoup.mvc.service.MvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MvcController {

    @Autowired
    MvcService mvcService;

//    @GetMapping("/logincheck")
//    public String loginCheck(){
//        return "logincheck";
//    }

    @GetMapping("/mvc/profile")
    public String profileAll(Model model){
        model.addAttribute("profiles", mvcService.getProfile());
        return "profile";
    }

    @GetMapping("/mvc/")
    public String index(){
        return "index";
    }

}
