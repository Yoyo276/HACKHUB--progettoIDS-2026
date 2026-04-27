package com.example.state;

import com.example.model.Hackathon;
import com.example.model.Team;

public class TerminatoState implements HackathonState {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        System.out.println("ERRORE: L'Hackathon è TERMINATO. Nessuna nuova iscrizione ammessa.");
    }

    @Override
    public void inviaSottomissione(Hackathon h, Team t, String link) {
        System.out.println("ERRORE: Evento concluso. Non puoi più inviare progetti.");
    }

    @Override
    public void procediAlProssimoStato(Hackathon h) {
        System.out.println("SISTEMA: L'Hackathon è già nello stato finale.");
    }

    @Override
    public String getNomeStato() {
        return "TERMINATO";
    }
}