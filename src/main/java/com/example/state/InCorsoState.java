package com.example.state;

import com.example.model.Hackathon;
import com.example.model.Team;

public class InCorsoState implements HackathonState {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        System.out.println("ERRORE: Iscrizioni chiuse!");
    }

    @Override
    public void inviaSottomissione(Hackathon h, Team t, String link) {
        t.setLinkSottomissione(link);
        System.out.println("SISTEMA: Progetto del team '" + t.getNome() + "' caricato.");
    }

    @Override
    public void procediAlProssimoStato(Hackathon h) {
        long consegnati = h.getTeams().stream()
                .filter(t -> t.getLinkSottomissione() != null).count();

        if (consegnati < 2) {
            System.out.println("ERRORE: Servono almeno 2 consegne.");
            return;
        }
        h.setStato(new InValutazioneState());
    }

    @Override
    public String getNomeStato() { return "IN CORSO"; }
}