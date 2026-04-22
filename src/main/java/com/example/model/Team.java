package com.example.model;

import com.example.model.auth.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String linkSottomissione;
    private Integer voto = 0;
    private String giudizioGiudice;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    @JsonIgnore
    private Hackathon hackathon;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<User> membri = new ArrayList<>();

    private String utenteInvitato;
    private boolean invitoAccettato;
    private String messaggioSupporto;
    private String orarioCall;
    private boolean premioErogato;

    public Team() {
    }

    public Team(String nome) {
        this.nome = nome;
    }
    
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLinkSottomissione() {
        return linkSottomissione;
    }

    public void setLinkSottomissione(String link) {
        this.linkSottomissione = link;
    }

    public Integer getVoto() {
        return voto;
    }

    public void setVoto(Integer voto) {
        this.voto = voto;
    }

    public String getGiudizioGiudice() {
        return giudizioGiudice;
    }

    public void setGiudizioGiudice(String g) {
        this.giudizioGiudice = g;
    }

    public Hackathon getHackathon() {
        return hackathon;
    }

    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }

    public List<User> getMembri() {
        return membri;
    }

    public void setMembri(List<User> membri) {
        this.membri = membri;
    }

    public String getUtenteInvitato() {
        return utenteInvitato;
    }

    public void setUtenteInvitato(String utenteInvitato) {
        this.utenteInvitato = utenteInvitato;
    }

    public boolean isInvitoAccettato() {
        return invitoAccettato;
    }

    public void setInvitoAccettato(boolean invitoAccettato) {
        this.invitoAccettato = invitoAccettato;
    }

    public String getMessaggioSupporto() {
        return messaggioSupporto;
    }

    public void setMessaggioSupporto(String messaggioSupporto) {
        this.messaggioSupporto = messaggioSupporto;
    }

    public String getOrarioCall() {
        return orarioCall;
    }

    public void setOrarioCall(String orarioCall) {
        this.orarioCall = orarioCall;
    }

    public boolean isPremioErogato() {
        return premioErogato;
    }

    public void setPremioErogato(boolean premioErogato) {
        this.premioErogato = premioErogato;
    }
}