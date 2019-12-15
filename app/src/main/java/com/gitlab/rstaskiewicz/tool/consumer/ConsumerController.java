package com.gitlab.rstaskiewicz.tool.consumer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @GetMapping("/test")
    String siema() {
        return "suema";
    }
}
