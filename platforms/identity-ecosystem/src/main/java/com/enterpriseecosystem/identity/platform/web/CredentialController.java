package com.enterpriseecosystem.identity.platform.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CredentialController {

    @RequestMapping(value = "/me/password", method = RequestMethod.GET)
    public String changePassword(Model model) {
        model.addAttribute("changePasswordForm", new ChangePasswordForm());
        return "me/password";
    }
}
