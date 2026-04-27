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

    @PostMapping("/crea-hackathon")
    @PreAuthorize("hasRole('ORGANIZZATORE')")
    public String creaHackathon(
            @RequestParam("nome") String nome,
            @RequestParam("luogo") String luogo,
            @RequestParam("scadenza") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scadenza,
            @RequestParam("inizio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inizio,
            @RequestParam("fine") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fine,
            @RequestParam("maxMembri") Integer maxMembri) {

        String usernameOrg = SecurityContextHolder.getContext().getAuthentication().getName();
        return facade.creaHackathon(nome, luogo, premio, giudiceId, usernameOrg, scadenza, inizio, fine, maxMembri);
    }

    @PostMapping("/avanza")
    @PreAuthorize("hasRole('ORGANIZZATORE')")
    public String avanza(@RequestParam("hackathonId") Long hackathonId) {
        return facade.avanza(hackathonId);
    }

    @PostMapping("/chiedi-supporto")
    @PreAuthorize("hasRole('MEMBRO_TEAM')")
    public String chiediSupporto(@RequestParam("id") Long id, @RequestParam("messaggio") String messaggio) {
        return facade.richiediAiuto(id, messaggio);
    }
    
    @GetMapping("/squadre-hackathon")
    public List<Team> getSquadreHackathon(@RequestParam("hackathonId") Long hackathonId) {
        return facade.getDettagliHackathon(hackathonId).getTeams();
    }

    @PostMapping("/accetta-invito")
    @PreAuthorize("hasRole('UTENTE')")
    public String accettaInvito(@RequestParam("teamId") Long teamId) {
        return facade.accettaInvito(teamId);
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

    @GetMapping("/stato-sottomissione")
    @PreAuthorize("hasRole('MEMBRO_TEAM')")
    public String visualizzaStatoSottomissione() {
        return facade.getStatoMiaSottomissione();
    }

    @GetMapping("/sottomissioni-hackathon")
    @PreAuthorize("hasAnyRole('ORGANIZZATORE', 'GIUDICE', 'MENTORE')")
    public List<String> accessoSottomissioni(@RequestParam("hackathonId") Long hackathonId) { // <-- SISTEMATO
        return facade.getLinkSottomissioni(hackathonId);
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

    @PostMapping("/sottometti")
    @PreAuthorize("hasRole('MEMBRO_TEAM')")
    public String sottometti(@RequestParam("id") Long id, @RequestParam("link") String link) {
        return facade.sottometti(id, link);
    }

    @GetMapping("/tutti-team")
    public List<Team> listaTeam() {
        return facade.getAll();
    }

}
