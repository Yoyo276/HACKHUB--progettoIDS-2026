package com.example.facade;

import com.example.model.Team;

public class HackHubFacade {


    public void erogaPremio(Team vincitore, double somma) {
        System.out.println("\n[SISTEMA ESTERNO PAGAMENTI]");
        System.out.println("Connessione sicura al gateway bancario...");
        System.out.println("Transazione completata: " + somma + "€ inviati al team " + vincitore.getNome());
    }

    
    public void registraEventoCalendario(String nomeEvento, String data) {
        System.out.println("\n[SISTEMA ESTERNO CALENDAR]");
        System.out.println("Sincronizzazione con Google/Outlook Calendar...");
        System.out.println("Evento '" + nomeEvento + "' registrato per il " + data);
    }
}