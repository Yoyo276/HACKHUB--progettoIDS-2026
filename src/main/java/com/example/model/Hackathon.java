package com.example.model;

import com.example.model.auth.User;
import jakarta.persistence.*;

@Entity
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String luogo;
    private Double premioInDenaro;

    @ManyToOne
    private User organizzatore;

    public Hackathon() {
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

    public User getOrganizzatore() {
        return organizzatore;
    }

    public void setOrganizzatore(User o) {
        this.organizzatore = o;
    }
}