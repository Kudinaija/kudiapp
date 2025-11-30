package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.services.EmailService;
import org.springframework.stereotype.Controller;

@Controller
public class EmailVerificationPageController {

    private final EmailService emailService;

    public EmailVerificationPageController(EmailService emailService) {
        this.emailService = emailService;
    }

//    @GetMapping("/verify-email")
//    public String verifyEmail(@RequestParam("token") String encodedToken, Model model) {
//        try {
//            GenericResponse response = emailService.verifyEmail(encodedToken);
//
//            model.addAttribute("success", true);
//            model.addAttribute("message", response.getMessage()); // "Verified Successfully"
//            model.addAttribute("loginUrl", "https://handyproshospitality.ng/login");
//            return "email-verification-result";
//
//        } catch (InvalidCredentialsException e) {
//            // Handle invalid token specifically
//            model.addAttribute("success", false);
//            model.addAttribute("message", "Invalid or expired verification link.");
//            model.addAttribute("signupUrl", "https://handyproshospitality.ng/signup");
//            return "email-verification-result";
//
//        } catch (Exception e) {
//            // Handle any other exceptions
//            model.addAttribute("success", false);
//            model.addAttribute("message", "An error occurred during verification. Please try again.");
//            model.addAttribute("signupUrl", "/signup");
//            return "email-verification-result";
//        }
//    }
}