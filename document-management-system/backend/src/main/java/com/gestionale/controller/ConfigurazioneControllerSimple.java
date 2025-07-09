package com.gestionale.controller;

import com.gestionale.entity.Configurazione;
import com.gestionale.service.ConfigurazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configurazioni")
public class ConfigurazioneControllerSimple {

    @Autowired
    private ConfigurazioneService configurazioneService;

    @GetMapping
    public List<Configurazione> getAllConfigurazioni() {
        return configurazioneService.getAllConfigurazioni();
    }

    @PostMapping
    public Configurazione createConfigurazione(@RequestBody Configurazione configurazione) {
        return configurazioneService.createConfigurazione(configurazione);
    }
}
