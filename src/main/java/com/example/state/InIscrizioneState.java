package com.example.state;

import com.example.model.Hackathon;
import com.example.model.Team;

public class InIscrizioneState implements HackathonState {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        // Aggiunge il team alla lista dell'hackathon
        h.getTeams().add(t);
        System.out.println("SISTEMA: Team " + t.getNome() + " iscritto.");
    }

    @Override
    public void inviaSottomissione(Hackathon h, Team t, String link) {
        System.out.println("ERRORE: Non puoi sottomettere progetti durante le iscrizioni.");
    }

    @Override
    public void procediAlProssimoStato(Hackathon h) {
        if (h.getTeams().size() < 2) {
            System.out.println("ERRORE: Servono almeno 2 team per iniziare.");
            return;
        }
        h.setStato(new InCorsoState());
    }

    @Override
    public String getNomeStato() {
        return "IN ISCRIZIONE";
    }
}