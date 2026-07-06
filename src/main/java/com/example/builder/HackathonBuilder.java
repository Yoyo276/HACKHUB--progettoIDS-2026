package com.example.builder;

import com.example.model.Hackathon;
import com.example.model.auth.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HackathonBuilder {

    private String nome;
    private String luogo;
    private Double premioInDenaro;
    private LocalDate dataScadenzaIscrizioni;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private Integer dimensioneMassimaTeam;
    private User organizzatore;
    private User giudiceAssegnato;
    private List<User> mentori = new ArrayList<>();

    public HackathonBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public HackathonBuilder luogo(String luogo) {
        this.luogo = luogo;
        return this;
    }

    public HackathonBuilder premioInDenaro(Double premioInDenaro) {
        this.premioInDenaro = premioInDenaro;
        return this;
    }

    public HackathonBuilder dataScadenzaIscrizioni(LocalDate data) {
        this.dataScadenzaIscrizioni = data;
        return this;
    }

    public HackathonBuilder dataInizio(LocalDate data) {
        this.dataInizio = data;
        return this;
    }

    public HackathonBuilder dataFine(LocalDate data) {
        this.dataFine = data;
        return this;
    }

    public HackathonBuilder dimensioneMassimaTeam(Integer dimensione) {
        this.dimensioneMassimaTeam = dimensione;
        return this;
    }

    public HackathonBuilder organizzatore(User organizzatore) {
        this.organizzatore = organizzatore;
        return this;
    }

    public HackathonBuilder giudiceAssegnato(User giudice) {
        this.giudiceAssegnato = giudice;
        return this;
    }

    public HackathonBuilder mentori(List<User> mentori) {
        this.mentori = mentori != null ? mentori : new ArrayList<>();
        return this;
    }

    public Hackathon build() {
        Hackathon h = new Hackathon();
        h.setNome(nome);
        h.setLuogo(luogo);
        h.setPremioInDenaro(premioInDenaro);
        h.setDataScadenzaIscrizioni(dataScadenzaIscrizioni);
        h.setDataInizio(dataInizio);
        h.setDataFine(dataFine);
        h.setDimensioneMassimaTeam(dimensioneMassimaTeam);
        h.setOrganizzatore(organizzatore);
        h.setGiudiceAssegnato(giudiceAssegnato);
        h.setMentori(mentori);
        // nomeStato default "IN ISCRIZIONE" già settato nel costruttore di Hackathon
        return h;
    }
}
