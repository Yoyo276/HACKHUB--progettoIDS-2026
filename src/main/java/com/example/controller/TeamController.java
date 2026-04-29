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

    // --- AZIONI PER L'ORGANIZZATORE ---

    @PostMapping("/crea-hackathon")
    @PreAuthorize("hasRole('ORGANIZZATORE')")
    public String creaHackathon(
            @RequestParam("nome") String nome,
            @RequestParam("luogo") String luogo,
            @RequestParam("premio") Double premio,
            @RequestParam("giudiceId") Long giudiceId,
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

    @PostMapping("/proclama-vincitore")
    @PreAuthorize("hasRole('ORGANIZZATORE')")
    public String proclamaVincitore(@RequestParam("hackathonId") Long hackathonId) {
        return facade.erogaPremioAutomatico(hackathonId);
    }

    @PostMapping("/chiedi-supporto")
    @PreAuthorize("hasRole('MEMBRO_TEAM')")
    public String chiediSupporto(@RequestParam("id") Long id, @RequestParam("messaggio") String messaggio) {
        return facade.richiediAiuto(id, messaggio);
    }
/* @GetMapping("/squadre-hackathon")
    public List<String> getSquadreHackathon(@RequestParam("hackathonId") Long hackathonId) {
        // Restituiamo solo i nomi dei team per questioni di privacy verso il pubblico
        return facade.getDettagliHackathon(hackathonId).getTeams().stream().map(Team::getNome).collect(Collectors.toList());
    }*/
    
    @GetMapping("/squadre-hackathon")
    public List<Team> getSquadreHackathon(@RequestParam("hackathonId") Long hackathonId) {
        // Restituisce l'intera lista degli oggetti Team con tutte le loro specifiche
        return facade.getDettagliHackathon(hackathonId).getTeams();
    }

    @PostMapping("/accetta-invito")
    @PreAuthorize("hasRole('UTENTE')")
    public String accettaInvito(@RequestParam("teamId") Long teamId) {
        return facade.accettaInvito(teamId);
    }

    @PostMapping("/segnala-violazione")
    @PreAuthorize("hasRole('MENTORE')")
    public String segnala(@RequestParam("id") Long id, @RequestParam("nota") String nota) {
        return facade.segnalaViolazione(id, nota);
    }

    @GetMapping("/dettagli-hackathon")
    public HackathonDTO visualizzaDettagli(@RequestParam("hackathonId") Long hackathonId) {
        // 1. Recupera l'entity dal database tramite la facade
        Hackathon h = facade.getDettagliHackathon(hackathonId);

        // 2. Se non esiste, potresti voler gestire l'errore,
        // ma qui lo trasformiamo direttamente nel DTO "leggero"
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

    // --- AZIONI PER L'UTENTE ---

    @PostMapping("/crea-team")
    @PreAuthorize("hasRole('UTENTE')")
    public String creaTeam(@RequestParam("hackathonId") Long hackathonId, @RequestParam("nome") String nome) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return facade.iscriviTeam(hackathonId, nome, username);
    }

    // --- AZIONI PER IL MEMBRO TEAM ---

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

    // --- AZIONI PER IL GIUDICE ---

    @PostMapping("/vota")
    @PreAuthorize("hasRole('GIUDICE')")
    public String vota(
            @RequestParam("id") Long id,
            @RequestParam("voto") int voto,
            @RequestParam("giudizio") String giudizio) {
        String usernameGiudice = SecurityContextHolder.getContext().getAuthentication().getName();
        return facade.daiVoto(id, voto, giudizio, usernameGiudice);
    }

    // --- CONSULTAZIONE ---

    @PostMapping("/aggiungi-mentore")
    @PreAuthorize("hasRole('ORGANIZZATORE')")
    public String aggiungiMentore(
            @RequestParam("hackathonId") Long hackathonId,
            @RequestParam("usernameMentore") String usernameMentore) {

        return facade.aggiungiMentore(hackathonId, usernameMentore);
    }

    // UC15 - Visualizza Richieste Supporto (Per Mentore/Organizzatore)
    @GetMapping("/richieste-supporto")
    @PreAuthorize("hasAnyRole('MENTORE', 'ORGANIZZATORE')")
    public List<String> vediRichieste(@RequestParam("hackathonId") Long hackathonId) {
        return facade.getMessaggiSupporto(hackathonId);
    }

    // UC16/17 - Proponi Call (Per Mentore)
    @PostMapping("/proponi-call")
    @PreAuthorize("hasRole('MENTORE')")
    public String proponiCall(@RequestParam("id") Long id, @RequestParam("orario") String orario) {
        return facade.pianificaCall(id, orario);
    }

    @GetMapping("/tutti-team")
    public List<Team> listaTeam() {
        return facade.getAll();
    }

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
}
