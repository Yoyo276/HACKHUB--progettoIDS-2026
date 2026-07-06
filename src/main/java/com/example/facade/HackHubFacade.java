package com.example.facade;

import com.example.model.Team;
import org.springframework.stereotype.Component;

@Component
public class HackHubFacade {

    // Nasconde la complessità del sistema di pagamento
    public void erogaPremio(Team vincitore, double somma) {
        System.out.println("\n[SISTEMA ESTERNO PAGAMENTI]");
        System.out.println("Connessione sicura al gateway bancario...");
        System.out.println("Transazione completata: " + somma + "€ inviati al team " + vincitore.getNome());
    }

    // Nasconde la complessità del calendario
    public void registraEventoCalendario(String nomeEvento, String data) {
        System.out.println("\n[SISTEMA ESTERNO CALENDAR]");
        System.out.println("Sincronizzazione con Google/Outlook Calendar...");
        System.out.println("Evento '" + nomeEvento + "' registrato per il " + data);
    }
}