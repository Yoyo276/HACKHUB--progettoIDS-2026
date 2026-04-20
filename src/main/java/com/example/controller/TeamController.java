package com.example.controller;

import com.example.dto.HackathonDTO;
import com.example.model.Hackathon;
import com.example.model.Team;
import com.example.service.HackathonFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private HackathonFacade facade;

    // --- CONSULTAZIONE PUBBLICA (Commit 6) ---

    @GetMapping("/tutti-hackathon")
    public List<HackathonDTO> getTuttiHackathon() {
        List<Hackathon> hackathons = facade.getTuttiHackathonEntity();
        return hackathons.stream()
                .map(h -> new HackathonDTO(
                        h.getId(),
                        h.getNome(),
                        h.getLuogo(),
                        h.getPremioInDenaro(),
                        h.getNomeStato(),
                        h.getDataScadenzaIscrizioni(),
                        h.getDimensioneMassimaTeam()))
                .collect(Collectors.toList());
    }

    @GetMapping("/dettagli-hackathon")
    public HackathonDTO visualizzaDettagli(@RequestParam("hackathonId") Long hackathonId) {
        Hackathon h = facade.getDettagliHackathon(hackathonId);
        return new HackathonDTO(
                h.getId(),
                h.getNome(),
                h.getLuogo(),
                h.getPremioInDenaro(),
                h.getNomeStato(),
                h.getDataScadenzaIscrizioni(),
                h.getDimensioneMassimaTeam());
    }

    @GetMapping("/squadre-hackathon")
    public List<Team> getSquadreHackathon(@RequestParam("hackathonId") Long hackathonId) {
        return facade.getDettagliHackathon(hackathonId).getTeams();
    }
}