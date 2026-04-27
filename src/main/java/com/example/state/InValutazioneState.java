package com.example.state;

import com.example.model.Hackathon;
import com.example.model.Team;

public class InValutazioneState implements HackathonState {

    @Override
    public void iscriviTeam(Hackathon h, Team t) {
        System.out.println("ERRORE: Iscrizioni chiuse. Siamo in fase di valutazione.");
    }

    @Override
    public void inviaSottomissione(Hackathon h, Team t, String link) {
        System.out.println("ERRORE: Tempo scaduto! Non si possono più inviare progetti.");
    }

@Override
public void procediAlProssimoStato(Hackathon h) {
    System.out.println("SISTEMA: Per chiudere l'hackathon, usa l'endpoint eroga-premio.");
}

    @Override
    public String getNomeStato() {
        return "IN VALUTAZIONE";
    }

    public void daiVoto(Team t, int voto) {
        if (voto >= 0 && voto <= 10) {
            System.out.println("GIUDICE: Il team " + t.getNome() + " ha ricevuto un voto di: " + voto + "/10");
        } else {
            System.out.println("ERRORE: Il voto deve essere compreso tra 0 e 10!");
        }
    }
}