package no.nav.pus_fss_frontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalController {

    @GetMapping("/isAlive")
    public void isAlive() {}

    @GetMapping("/isReady")
    public void isReady() {}

}
