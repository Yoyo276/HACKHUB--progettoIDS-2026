package com.example.model;

import com.example.model.auth.User;
import com.example.state.HackathonState;
import com.example.state.InIscrizioneState;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String luogo;
    private Double premioInDenaro;
    private String nomeStato = "IN ISCRIZIONE";

    // --- NUOVI CAMPI PER IL CALENDAR E REGOLE ---
    private LocalDate dataScadenzaIscrizioni;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private Integer dimensioneMassimaTeam;

    @Transient
    private HackathonState statoAttuale;

    @ManyToOne
    private User organizzatore;

    @ManyToOne
    private User giudiceAssegnato;

    // --- NUOVO: LISTA DI MENTORI ---
    @ManyToMany(fetch = FetchType.EAGER)
    private List<User> mentori = new ArrayList<>();

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("hackathon")
    private List<Team> teams = new ArrayList<>();

    public Hackathon() {
        this.statoAttuale = new InIscrizioneState();
    }

    public HackathonState getStato() {
        return statoAttuale;
    }

    public void setStato(HackathonState nuovoStato) {
        this.statoAttuale = nuovoStato;
        this.nomeStato = nuovoStato.getNomeStato();
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String l) {
        this.luogo = l;
    }

    public Double getPremioInDenaro() {
        return premioInDenaro;
    }

    public void setPremioInDenaro(Double p) {
        this.premioInDenaro = p;
    }

    public String getNomeStato() {
        return nomeStato;
    }

    public void setNomeStato(String s) {
        this.nomeStato = s;
    }

    public LocalDate getDataScadenzaIscrizioni() {
        return dataScadenzaIscrizioni;
    }

    public void setDataScadenzaIscrizioni(LocalDate dataScadenzaIscrizioni) {
        this.dataScadenzaIscrizioni = dataScadenzaIscrizioni;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public Integer getDimensioneMassimaTeam() {
        return dimensioneMassimaTeam;
    }

    public void setDimensioneMassimaTeam(Integer dimensioneMassimaTeam) {
        this.dimensioneMassimaTeam = dimensioneMassimaTeam;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public User getOrganizzatore() {
        return organizzatore;
    }

    public void setOrganizzatore(User o) {
        this.organizzatore = o;
    }

    public User getGiudiceAssegnato() {
        return giudiceAssegnato;
    }

    public void setGiudiceAssegnato(User g) {
        this.giudiceAssegnato = g;
    }

    public List<User> getMentori() {
        return mentori;
    }

    public void setMentori(List<User> mentori) {
        this.mentori = mentori;
    }
}
