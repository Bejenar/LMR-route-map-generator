package com.whiteroom.frontend.controller;

import com.whiteroom.frontend.service.RouteBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RoutemapController {

    private final RouteBuilderService routeBuilderService;


    @GetMapping("/routes")
    public String routes(Model model) {
        model.addAttribute("days", routeBuilderService.getDays());
        return "route-map";
    }

    @GetMapping("/")
    public String ooo(Model model) {
        model.addAttribute("start",routeBuilderService.traceRoute().stream().findFirst().get());
        return "index";
    }
}
