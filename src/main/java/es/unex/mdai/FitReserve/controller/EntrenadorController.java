// java
package es.unex.mdai.FitReserve.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntrenadorController {

    @GetMapping("/entrenador")
    public String entrenadorPage() {
        return "entrenadorPage";
    }
}
