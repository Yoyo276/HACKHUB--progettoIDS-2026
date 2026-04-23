package com.example.controller;

import com.example.model.Team;
import com.example.dto.HackathonDTO;
import com.example.model.Hackathon;
import com.example.service.HackathonFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private HackathonFacade facade;

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


    @GetMapping("/squadre-hackathon")
    public List<Team> getSquadreHackathon(@RequestParam("hackathonId") Long hackathonId) {
        return facade.getDettagliHackathon(hackathonId).getTeams();
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

    @PostMapping("/crea-team")
    @PreAuthorize("hasRole('UTENTE')")
    public String creaTeam(@RequestParam("hackathonId") Long hackathonId, @RequestParam("nome") String nome) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return facade.iscriviTeam(hackathonId, nome, username);
    }

    @PostMapping("/invita")
    @PreAuthorize("hasRole('MEMBRO_TEAM')")
    public String invita(@RequestParam("id") Long id, @RequestParam("user") String user) {
        return facade.invita(id, user);
    }

    @PostMapping("/accetta-invito")
    @PreAuthorize("hasRole('UTENTE')")
    public String accettaInvito(@RequestParam("teamId") Long teamId) {
        return facade.accettaInvito(teamId);
    }

    @GetMapping("/tutti-team")
    public List<Team> listaTeam() {
        return facade.getAll();
    }


}