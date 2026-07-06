package com.example.state;

import com.example.model.Hackathon;
import com.example.model.Team;

/**
 * Questa è l'interfaccia base del Design Pattern State.
 * Definisce i metodi che cambieranno comportamento a seconda della fase dell'Hackathon.
 */
public interface HackathonState {
    
    // Metodo per iscrivere un team (permesso solo in "InIscrizione")
    void iscriviTeam(Hackathon h, Team t);
    
    // Metodo per inviare il progetto (permesso solo in "InCorso")
    void inviaSottomissione(Hackathon h, Team t, String link);
    
    // Metodo per passare alla fase successiva (gestito da ogni stato)
    void procediAlProssimoStato(Hackathon h);
    
    // Restituisce il nome dello stato per la UI
    String getNomeStato();
}