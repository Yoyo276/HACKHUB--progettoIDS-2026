package com.example.dto;

import java.time.LocalDate;

public class HackathonDTO {
    private Long id;
    private String nome;
    private String luogo;
    private Double premioInDenaro;
    private String nomeStato;
    private LocalDate dataScadenzaIscrizioni;
    private Integer dimensioneMassimaTeam;

    // Costruttore per mappare facilmente l'Entity
    public HackathonDTO(Long id, String nome, String luogo, Double premioInDenaro, 
                        String nomeStato, LocalDate dataScadenzaIscrizioni, Integer dimensioneMassimaTeam) {
        this.id = id;
        this.nome = nome;
        this.luogo = luogo;
        this.premioInDenaro = premioInDenaro;
        this.nomeStato = nomeStato;
        this.dataScadenzaIscrizioni = dataScadenzaIscrizioni;
        this.dimensioneMassimaTeam = dimensioneMassimaTeam;
    }

    // Getter e Setter (Necessari per Jackson)
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getLuogo() { return luogo; }
    public Double getPremioInDenaro() { return premioInDenaro; }
    public String getNomeStato() { return nomeStato; }
    public LocalDate getDataScadenzaIscrizioni() { return dataScadenzaIscrizioni; }
    public Integer getDimensioneMassimaTeam() { return dimensioneMassimaTeam; }
}