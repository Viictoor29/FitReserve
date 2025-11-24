package es.unex.mdai.FitReserve.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {

    public ErrorController() {
    }

    @GetMapping({"/error"})
    public String index() {
        return "error";
    }
}
