package com.example.repository;

import com.example.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    List<Hackathon> findByNomeStato(String nomeStato);
    List<Hackathon> findByGiudiceAssegnato_Username(String username);
}
