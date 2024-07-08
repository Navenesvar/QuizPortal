package com.naven.quizzer.controller;

import com.naven.quizzer.domain.SignUp;
import com.naven.quizzer.domain.User;
import com.naven.quizzer.service.SignUpService;
import com.naven.quizzer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private SignUpService signUpService;

    @GetMapping("/")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, Model model) {
        User existingUser = userService.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (existingUser != null) {
            String username = user.getUsername();
            model.addAttribute("username", existingUser.getUsername());
            return "redirect:/dashboard?username=" + username;
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String showSignUpForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@RequestParam("username") String username,
                     @RequestParam("email") String email,
                     @RequestParam("password") String password,
                     @RequestParam("confirmPassword") String confirmPassword, Model model) {
        if (signUpService.existsByUsername(username) || signUpService.existsByEmail(email)) {
            model.addAttribute("error", "User exists");
            return "signup";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords don't match");
            return "signup";
        }
        try {
            SignUp signUp = new SignUp(username, email, password, confirmPassword);
            signUpService.save(signUp);
            User login = new User(username,password);
            userService.save(login);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    return "redirect:/login";
    }
    @GetMapping("/dashboard")
    public String display(Model model, @RequestParam("username") String username) {
        model.addAttribute("username", username);
        return "dashboard";
    }
    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
