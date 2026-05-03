package com.example.service;

import com.example.model.*;
import com.example.model.auth.Role;
import com.example.model.auth.User;
import com.example.repository.*; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HackathonFacade {

    @Autowired
    private HackathonService hackathonService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private HackathonRepository hackathonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // --- 1. GESTIONE HACKATHON E STAFF (ORGANIZZATORE) ---

    public String creaHackathon(String nome, String luogo, Double premio, Long giudiceId, String usernameOrg,
                               LocalDate scadenza, LocalDate inizio, LocalDate fine, Integer maxMembri) {
        User org = userRepository.findByUsername(usernameOrg).orElse(null);
        User giudice = userRepository.findById(giudiceId).orElse(null);

        Hackathon h = new Hackathon();
        h.setNome(nome);
        h.setLuogo(luogo);
        h.setPremioInDenaro(premio);
        h.setOrganizzatore(org);
        h.setGiudiceAssegnato(giudice);
        h.setNomeStato("IN ISCRIZIONE");
        h.setDataScadenzaIscrizioni(scadenza);
        h.setDataInizio(inizio);
        h.setDataFine(fine);
        h.setDimensioneMassimaTeam(maxMembri);

        hackathonRepository.save(h);
        return "Hackathon '" + nome + "' creato. Scadenza: " + scadenza;
    }

    public List<Hackathon> getTuttiHackathonEntity() {
        return hackathonService.findAll(); 
    }

    public String aggiungiMentore(Long hackathonId, String usernameMentore) {
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        User mentore = userRepository.findByUsername(usernameMentore).orElse(null);

        if (h != null && mentore != null) {
            h.getMentori().add(mentore);
            hackathonRepository.save(h);
            return "Mentore " + usernameMentore + " aggiunto all'hackathon " + h.getNome();
        }
        return "Errore: Hackathon o Mentore non trovato.";
    }

    public String avanza(Long hackathonId) {
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        if (h == null) return "Errore: Hackathon non trovato.";

        if (h.getNomeStato().equals("IN ISCRIZIONE"))
            h.setNomeStato("IN CORSO");
        else if (h.getNomeStato().equals("IN CORSO"))
            h.setNomeStato("IN VALUTAZIONE");
        else if (h.getNomeStato().equals("IN VALUTAZIONE"))
            return erogaPremioAutomatico(hackathonId);

        hackathonRepository.save(h);
        return "Stato aggiornato a: " + h.getNomeStato();
    }

    // --- 2. GESTIONE TEAM (UTENTE / MEMBRO) ---

    @Transactional
    public String iscriviTeam(Long hackathonId, String nomeTeam, String usernameCapitano) {
        User capitano = userRepository.findByUsername(usernameCapitano).orElse(null);
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);

        if (h == null || capitano == null) return "Errore dati mancanti.";
        if (capitano.getTeam() != null) return "Errore: Fai già parte di un team!";
        if (!h.getNomeStato().equals("IN ISCRIZIONE")) return "Iscrizioni chiuse.";

        if (h.getDataScadenzaIscrizioni() != null && LocalDate.now().isAfter(h.getDataScadenzaIscrizioni())) {
            return "Errore: Il termine per le iscrizioni è scaduto.";
        }

        Team nuovoTeam = new Team();
        nuovoTeam.setNome(nomeTeam); 
        nuovoTeam.setHackathon(h);
        nuovoTeam = teamRepository.save(nuovoTeam);

        Role ruoloMembro = roleRepository.findByName("ROLE_MEMBRO_TEAM")
                .orElseThrow(() -> new RuntimeException("Ruolo ROLE_MEMBRO_TEAM non trovato nel DB!"));

        capitano.getRoles().clear();
        capitano.getRoles().add(ruoloMembro);
        capitano.setTeam(nuovoTeam);
        
        userRepository.save(capitano);

        return "Team iscritto! Sei diventato MEMBRO_TEAM. Fai LOGIN per aggiornare il token.";
    }

    public String invita(Long teamId, String usernameInvitato) {
        return teamRepository.findById(teamId).map(t -> {
            if (t.getMembri().size() >= t.getHackathon().getDimensioneMassimaTeam()) {
                return "Errore: Team al completo.";
            }
            t.setUtenteInvitato(usernameInvitato);
            teamRepository.save(t);
            return "Invito inviato a " + usernameInvitato;
        }).orElse("Team non trovato.");
    }

    public String sottometti(Long teamId, String link) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Team non trovato.";

        Hackathon h = t.getHackathon();
        if (!h.getNomeStato().equals("IN CORSO")) return "Sottomissione possibile solo in stato IN CORSO.";
        if (h.getDataFine() != null && LocalDate.now().isAfter(h.getDataFine())) {
            return "Errore: Data fine hackathon superata (" + h.getDataFine() + ")";
        }

        t.setLinkSottomissione(link);
        teamRepository.save(t);
        return "Progetto consegnato: " + link;
    }

    public String richiediAiuto(Long teamId, String messaggio) {
        Team t = teamRepository.findById(teamId).orElse(null);
        if (t == null) return "Team non trovato.";
        t.setMessaggioSupporto(messaggio);
        teamRepository.save(t);
        return "Richiesta inviata ai mentori: " + messaggio;
    }

    @Transactional
public String accettaInvito(Long teamId) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User u = userRepository.findByUsername(username).orElseThrow();
    Team t = teamRepository.findById(teamId).orElseThrow();

    if (!username.equals(t.getUtenteInvitato())) return "Errore: Non hai un invito pendente.";
    if (u.getTeam() != null) return "Errore: Fai già parte di un altro team!";

    // --- Aggiorniamo lo stato del Team ---
    t.setInvitoAccettato(true); 
    
    Role ruoloMembro = roleRepository.findByName("ROLE_MEMBRO_TEAM").orElseThrow();
    u.getRoles().clear();
    u.getRoles().add(ruoloMembro);
    u.setTeam(t);
    
    userRepository.save(u);
    teamRepository.save(t); // Salviamo le modifiche al Team

    return "Benvenuto nel team " + t.getNome() + "!";
}

    public String segnalaViolazione(Long teamId, String nota) {
        Team t = teamRepository.findById(teamId).orElseThrow();
        System.out.println("!!! SEGNALAZIONE !!! Team: " + t.getNome() + " Nota: " + nota);
        return "Violazione segnalata all'organizzatore.";
    }

    public String daiVoto(Long teamId, int voto, String giudizio, String usernameGiudice) {
        Team t = teamRepository.findById(teamId).orElse(null);
        User giudice = userRepository.findByUsername(usernameGiudice).orElse(null);

        if (t == null || giudice == null) return "Dati errati.";
        if (!t.getHackathon().getGiudiceAssegnato().getId().equals(giudice.getId())) {
            return "ERRORE: Non sei il giudice assegnato!";
        }
        if (!t.getHackathon().getNomeStato().equals("IN VALUTAZIONE")) return "Valutazioni chiuse.";

        t.setVoto(voto);
        t.setGiudizioGiudice(giudizio);
        teamRepository.save(t);
        return "Voto registrato.";
    }

    public String erogaPremioAutomatico(Long hackathonId) {
    Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
    if (h == null) return "Hackathon non trovato.";
    if (h.getTeams() == null || h.getTeams().isEmpty()) {
        return "Nessun team iscritto a questo hackathon.";
    }

    Team vincitore = h.getTeams().stream()
            .max(Comparator.comparingInt(t -> (t.getVoto() != null) ? t.getVoto() : 0))
            .orElse(null);

    if (vincitore != null && vincitore.getVoto() != null && vincitore.getVoto() > 0) {
        vincitore.setPremioErogato(true);
        teamRepository.save(vincitore);
        
        h.setNomeStato("CONCLUSO");
        hackathonRepository.save(h);
        
        return "Premio erogato al team " + vincitore.getNome() + " con voto: " + vincitore.getVoto();
    }
    
    return "Nessun vincitore proclamabile (voti mancanti o tutti a zero).";
}

    public List<String> getLinkSottomissioni(Long hackathonId) {
        String usernameLoggato = SecurityContextHolder.getContext().getAuthentication().getName();
        User utenteLoggato = userRepository.findByUsername(usernameLoggato).orElseThrow();
        Hackathon h = hackathonRepository.findById(hackathonId).orElseThrow();

        boolean isStaff = h.getOrganizzatore().getId().equals(utenteLoggato.getId()) ||
                          (h.getGiudiceAssegnato() != null && h.getGiudiceAssegnato().getId().equals(utenteLoggato.getId())) ||
                          h.getMentori().stream().anyMatch(m -> m.getId().equals(utenteLoggato.getId()));

        if (!isStaff) throw new RuntimeException("Accesso negato!");

        return h.getTeams().stream()
                .map(t -> "Team: " + t.getNome() + " | Link: " + (t.getLinkSottomissione() != null ? t.getLinkSottomissione() : "Nessuna"))
                .collect(Collectors.toList());
    }

    public String getStatoMiaSottomissione() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userRepository.findByUsername(username).orElseThrow();
        if (u.getTeam() == null) return "Nessun team.";
        Team t = u.getTeam();
        return "Link: " + (t.getLinkSottomissione() != null ? t.getLinkSottomissione() : "Nessuno") + 
               (t.getVoto() != null ? " | Voto: " + t.getVoto() : "");
    }

    // --- Il mentore vede le richieste di aiuto ---
public List<String> getMessaggiSupporto(Long hackathonId) {
    Hackathon h = hackathonRepository.findById(hackathonId)
            .orElseThrow(() -> new RuntimeException("Hackathon non trovato"));

    return h.getTeams().stream()
            .filter(t -> t.getMessaggioSupporto() != null && !t.getMessaggioSupporto().isEmpty())
            .map(t -> "Team: " + t.getNome() + " | Richiesta: " + t.getMessaggioSupporto())
            .collect(Collectors.toList());
}

// --- Il mentore propone la call ---
public String pianificaCall(Long teamId, String orario) {
    Team t = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team non trovato"));
    t.setOrarioCall(orario);
    
    String linkFinto = "https://meet.google.com/" + t.getNome().toLowerCase().replaceAll("\\s+", "-") + "-call";

    teamRepository.save(t);

    return "Call programmata con il " + t.getNome() + " per le ore " + orario + ". Link Meet: " + linkFinto;
}

    public Hackathon getDettagliHackathon(Long hackathonId) {
        return hackathonRepository.findById(hackathonId).orElseThrow();
    }

    public List<Team> getAll() { return teamRepository.findAll(); }
}