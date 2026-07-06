package com.example.service;

import com.example.builder.HackathonBuilder;
import com.example.facade.HackHubFacade;
import com.example.model.Hackathon;
import com.example.model.Team;
import com.example.model.auth.Role;
import com.example.model.auth.User;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HackathonManager {

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HackHubFacade hackHubFacade;

    // --- GESTIONE HACKATHON (ORGANIZZATORE) ---

    public String creaHackathon(String nome, String luogo, Double premio, Long giudiceId, String usernameOrg,
                                LocalDate scadenza, LocalDate inizio, LocalDate fine, Integer maxMembri) {
        User org = userRepository.findByUsername(usernameOrg).orElse(null);
        User giudice = userRepository.findById(giudiceId).orElse(null);

        if (org == null || giudice == null) return "Errore: Organizzatore o Giudice non trovato.";

        Hackathon h = new HackathonBuilder()
                .nome(nome)
                .luogo(luogo)
                .premioInDenaro(premio)
                .organizzatore(org)
                .giudiceAssegnato(giudice)
                .dataScadenzaIscrizioni(scadenza)
                .dataInizio(inizio)
                .dataFine(fine)
                .dimensioneMassimaTeam(maxMembri)
                .build();

        hackathonRepository.save(h);
        return "Hackathon '" + nome + "' creato. Scadenza iscrizioni: " + scadenza;
    }

    public List<Hackathon> getTuttiHackathonEntity() {
        return hackathonRepository.findAll();
    }

    public Hackathon getDettagliHackathon(Long hackathonId) {
        return hackathonRepository.findById(hackathonId).orElseThrow();
    }

    public Hackathon findById(Long id) {
        return hackathonRepository.findById(id).orElse(null);
    }

    public void save(Hackathon h) {
        hackathonRepository.save(h);
    }

    public String aggiungiMentore(Long hackathonId, String usernameMentore) {
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        User mentore = userRepository.findByUsername(usernameMentore).orElse(null);

        if (h == null) return "Errore: Hackathon non trovato.";
        if (mentore == null) return "Errore: Mentore non trovato.";

        h.getMentori().add(mentore);
        hackathonRepository.save(h);
        return "Mentore " + usernameMentore + " aggiunto all'hackathon " + h.getNome();
    }

    public String avanza(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        if (h == null) return "Errore: Hackathon non trovato.";

        String statoCorrente = h.getNomeStato();
        if (statoCorrente.equals("IN ISCRIZIONE")) {
            h.setNomeStato("IN CORSO");
        } else if (statoCorrente.equals("IN CORSO")) {
            h.setNomeStato("IN VALUTAZIONE");
        } else {
            return "Errore: L'hackathon è già in stato '" + statoCorrente + "' e non può avanzare automaticamente.";
        }

        hackathonRepository.save(h);
        return "Stato aggiornato a: " + h.getNomeStato();
    }

    public String proclamaVincitore(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        if (h == null) return "Errore: Hackathon non trovato.";
        if (!h.getNomeStato().equals("IN VALUTAZIONE")) return "Errore: L'hackathon non è in stato IN VALUTAZIONE.";
        if (h.getTeams() == null || h.getTeams().isEmpty()) return "Errore: Nessun team iscritto.";

        boolean tutteValutate = h.getTeams().stream().allMatch(t -> t.getVoto() != null);
        if (!tutteValutate) return "Errore: Non tutte le sottomissioni sono state valutate dal Giudice.";

        Team vincitore = h.getTeams().stream()
                .max(Comparator.comparingInt(t -> t.getVoto()))
                .orElse(null);

        if (vincitore == null) return "Errore: Impossibile determinare il vincitore.";

        hackHubFacade.erogaPremio(vincitore, h.getPremioInDenaro());
        vincitore.setPremioErogato(true);
        teamRepository.save(vincitore);

        h.setNomeStato("CONCLUSO");
        hackathonRepository.save(h);

        return "Premio erogato al team '" + vincitore.getNome() + "' (voto: " + vincitore.getVoto() + "). Hackathon concluso.";
    }

    // --- GESTIONE TEAM (UTENTE) ---

    @Transactional
    public String iscriviTeam(Long hackathonId, String nomeTeam, String usernameCapitano) {
        User capitano = userRepository.findByUsername(usernameCapitano).orElse(null);
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);

        if (h == null || capitano == null) return "Errore: dati mancanti.";
        if (capitano.getTeam() != null) return "Errore: fai già parte di un team.";
        if (!h.getNomeStato().equals("IN ISCRIZIONE")) return "Errore: iscrizioni chiuse.";
        if (h.getDataScadenzaIscrizioni() != null && LocalDate.now().isAfter(h.getDataScadenzaIscrizioni()))
            return "Errore: il termine per le iscrizioni è scaduto.";

        boolean nomeEsistente = h.getTeams().stream().anyMatch(t -> t.getNome().equalsIgnoreCase(nomeTeam));
        if (nomeEsistente) return "Errore: nome team già in uso per questo hackathon.";

        Team nuovoTeam = new Team();
        nuovoTeam.setNome(nomeTeam);
        nuovoTeam.setHackathon(h);
        nuovoTeam = teamRepository.save(nuovoTeam);

        Role ruoloMembro = roleRepository.findByName("ROLE_MEMBRO_TEAM")
                .orElseThrow(() -> new RuntimeException("Ruolo ROLE_MEMBRO_TEAM non trovato nel DB"));

        capitano.getRoles().clear();
        capitano.getRoles().add(ruoloMembro);
        capitano.setTeam(nuovoTeam);
        userRepository.save(capitano);

        return "Team '" + nomeTeam + "' iscritto. Sei diventato MEMBRO_TEAM. Fai LOGIN per aggiornare il token.";
    }

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    // --- INVITI (MEMBRO TEAM) ---

    public String invita(Long teamId, String usernameInvitato) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Errore: team non trovato.";
        if (t.getMembri().size() >= t.getHackathon().getDimensioneMassimaTeam())
            return "Errore: team al completo.";

        t.setUtenteInvitato(usernameInvitato);
        teamRepository.save(t);
        return "Invito inviato a " + usernameInvitato;
    }

    @Transactional
    public String accettaInvito(Long teamId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(username).orElseThrow();
        Team t = teamRepository.findById(teamId).orElseThrow();

        if (!username.equals(t.getUtenteInvitato())) return "Errore: non hai un invito pendente per questo team.";
        if (u.getTeam() != null) return "Errore: fai già parte di un altro team.";

        t.setInvitoAccettato(true);

        Role ruoloMembro = roleRepository.findByName("ROLE_MEMBRO_TEAM").orElseThrow();
        u.getRoles().clear();
        u.getRoles().add(ruoloMembro);
        u.setTeam(t);

        userRepository.save(u);
        teamRepository.save(t);

        return "Benvenuto nel team '" + t.getNome() + "'!";
    }

    // --- SOTTOMISSIONI (MEMBRO TEAM) ---

    public String sottometti(Long teamId, String link) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Errore: team non trovato.";

        Hackathon h = t.getHackathon();
        if (!h.getNomeStato().equals("IN CORSO")) return "Errore: sottomissione possibile solo in stato IN CORSO.";
        if (h.getDataFine() != null && LocalDate.now().isAfter(h.getDataFine()))
            return "Errore: data fine hackathon superata (" + h.getDataFine() + ").";

        t.setLinkSottomissione(link);
        teamRepository.save(t);
        return "Sottomissione caricata: " + link;
    }

    public String getStatoMiaSottomissione() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(username).orElseThrow();
        if (u.getTeam() == null) return "Nessun team associato.";
        Team t = u.getTeam();
        String stato = "Link: " + (t.getLinkSottomissione() != null ? t.getLinkSottomissione() : "nessuno");
        if (t.getVoto() != null) stato += " | Voto: " + t.getVoto() + " | Giudizio: " + t.getGiudizioGiudice();
        return stato;
    }

    public List<String> getLinkSottomissioni(Long hackathonId) {
        String usernameLoggato = SecurityContextHolder.getContext().getAuthentication().getName();
        User utenteLoggato = userRepository.findByUsername(usernameLoggato).orElseThrow();
        Hackathon h = hackathonRepository.findById(hackathonId).orElseThrow();

        boolean isStaff = h.getOrganizzatore().getId().equals(utenteLoggato.getId()) ||
                (h.getGiudiceAssegnato() != null && h.getGiudiceAssegnato().getId().equals(utenteLoggato.getId())) ||
                h.getMentori().stream().anyMatch(m -> m.getId().equals(utenteLoggato.getId()));

        if (!isStaff) throw new RuntimeException("Accesso negato: non sei staff di questo hackathon.");

        return h.getTeams().stream()
                .map(t -> "Team: " + t.getNome() + " | Link: " + (t.getLinkSottomissione() != null ? t.getLinkSottomissione() : "Nessuna"))
                .collect(Collectors.toList());
    }

    // --- VALUTAZIONE (GIUDICE) ---

    public String daiVoto(Long teamId, int voto, String giudizio, String usernameGiudice) {
        Team t = teamRepository.findById(teamId).orElse(null);
        User giudice = userRepository.findByUsername(usernameGiudice).orElse(null);

        if (t == null || giudice == null) return "Errore: dati non validi.";
        if (!t.getHackathon().getGiudiceAssegnato().getId().equals(giudice.getId()))
            return "Errore: non sei il giudice assegnato a questo hackathon.";
        if (!t.getHackathon().getNomeStato().equals("IN VALUTAZIONE"))
            return "Errore: l'hackathon non è in stato IN VALUTAZIONE.";
        if (t.getVoto() != null)
            return "Errore: questa sottomissione è già stata valutata.";

        t.setVoto(voto);
        t.setGiudizioGiudice(giudizio);
        teamRepository.save(t);
        return "Valutazione registrata per il team '" + t.getNome() + "'.";
    }

    // --- SUPPORTO E MENTORI ---

    public String richiediAiuto(Long teamId, String messaggio) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Errore: team non trovato.";
        t.setMessaggioSupporto(messaggio);
        teamRepository.save(t);
        return "Richiesta di supporto inviata: " + messaggio;
    }

    public List<String> getMessaggiSupporto(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        return h.getTeams().stream()
                .filter(t -> t.getMessaggioSupporto() != null && !t.getMessaggioSupporto().isEmpty())
                .map(t -> "Team: " + t.getNome() + " | Richiesta: " + t.getMessaggioSupporto())
                .collect(Collectors.toList());
    }

    public String pianificaCall(Long teamId, String orario) {
        Team t = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team non trovato"));
        t.setOrarioCall(orario);
        teamRepository.save(t);

        hackHubFacade.registraEventoCalendario("Call con " + t.getNome(), orario);

        String linkMeet = "https://meet.google.com/" + t.getNome().toLowerCase().replaceAll("\\s+", "-") + "-call";
        return "Call programmata con il team '" + t.getNome() + "' per le ore " + orario + ". Link: " + linkMeet;
    }

    public String aggiornaLinkSottomissione(Long teamId, String nuovoLink) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Errore: team non trovato.";

        Hackathon h = t.getHackathon();
        if (!h.getNomeStato().equals("IN CORSO")) return "Errore: aggiornamento possibile solo in stato IN CORSO.";
        if (h.getDataFine() != null && LocalDate.now().isAfter(h.getDataFine()))
            return "Errore: data fine hackathon superata.";
        if (t.getLinkSottomissione() == null) return "Errore: nessuna sottomissione esistente da aggiornare.";

        t.setLinkSottomissione(nuovoLink);
        teamRepository.save(t);
        return "Sottomissione aggiornata: " + nuovoLink;
    }

    public List<String> getInvitiPerUtente(String username) {
        List<Team> teams = teamRepository.findByUtenteInvitato(username);
        return teams.stream()
                .filter(t -> !t.isInvitoAccettato())
                .map(t -> "Invito dal team '" + t.getNome() + "' (teamId: " + t.getId() + ")")
                .collect(Collectors.toList());
    }

    public String registraUtente(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent())
            return "Errore: username già in uso.";
        if (userRepository.findByEmail(email).isPresent())
            return "Errore: email già in uso.";

        Role ruoloUtente = roleRepository.findByName("ROLE_UTENTE")
                .orElseThrow(() -> new RuntimeException("Ruolo ROLE_UTENTE non trovato"));

        User nuovoUtente = new User();
        nuovoUtente.setUsername(username);
        nuovoUtente.setPassword(password);
        nuovoUtente.setEmail(email);
        nuovoUtente.getRoles().add(ruoloUtente);
        userRepository.save(nuovoUtente);

        return "Utente '" + username + "' registrato con successo.";
    }

    public String segnalaViolazione(Long teamId, String nota) {
        Team t = teamRepository.findById(teamId).orElseThrow();
        t.setNotaViolazione(nota);
        teamRepository.save(t);
        return "Violazione segnalata all'organizzatore per il team '" + t.getNome() + "'.";
    }

    public List<String> getSegnalazioni(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

        return h.getTeams().stream()
                .filter(t -> t.getNotaViolazione() != null && !t.getNotaViolazione().isEmpty())
                .map(t -> "Team: " + t.getNome() + " | Segnalazione: " + t.getNotaViolazione())
                .collect(Collectors.toList());
    }
}
