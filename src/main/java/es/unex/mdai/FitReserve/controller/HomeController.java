package es.unex.mdai.FitReserve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    public HomeController() {
    }

    @GetMapping({"/login"})
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
