package com.smart.smartcontactmanger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanger.DAO.UserRepository;
import com.smart.smartcontactmanger.entity.User;
import com.smart.smartcontactmanger.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @RequestMapping("/")
   public String home(Model model){
    model.addAttribute("title", "Home - Smart Contact Manger");
    return "home";
   } 
    @RequestMapping("/about")
   public String about(Model model){
    model.addAttribute("title", "About");
    return "about";
   } 

   //handler for custom login
   @GetMapping("/signin")
   public String customlogin(Model model){
    model.addAttribute("title", "Login Page");
    return "login";
   }

   @RequestMapping("/signup")
   public String signup(Model model){
    model.addAttribute("title","Register");
    model.addAttribute("user", new User());
    return "signup";
   }
   //handler for registerign user
   @PostMapping("/do_register")
   public String registerUser(@Valid @ModelAttribute("user")User user,BindingResult result,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,
   Model model,HttpSession session){
    try {
        if(!agreement){
            throw new Exception("You have not agreed the terms and conditions");
        }
        if(result.hasErrors()){
            System.out.println("ERROR "+result.toString());
            model.addAttribute("user", user);
            return "signup";
        }
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setImageurl("default.png");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Agreement "+agreement);
        System.out.println("User "+user);
        this.userRepository.save(user);
        model.addAttribute("user", new User());
        session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
        return "signup";
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("user", user);
        session.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
        return "signup";
    }
   }
}
