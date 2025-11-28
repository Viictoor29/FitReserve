package es.unex.mdai.FitReserve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectUsrController {

    @GetMapping("/admin")
    public String adminPage() {
        return "adminPage";
    }

    @GetMapping("/entrenador")
    public String entrenadorPage() {
        return "entrenadorPage";
    }

    @GetMapping("/cliente")
    public String clientePage() {
        return "clientePage";
    }
}
