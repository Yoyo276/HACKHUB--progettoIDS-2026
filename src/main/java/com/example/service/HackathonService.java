package com.example.service;

import com.example.model.Hackathon;
import com.example.repository.HackathonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service 
public class HackathonService {

    @Autowired
    private HackathonRepository hackathonRepository;

    public List<Hackathon> findAll() {
        return hackathonRepository.findAll();
    }

    public Hackathon findById(Long id) {
        return hackathonRepository.findById(id).orElse(null);
    }
    
    public void save(Hackathon h) {
        hackathonRepository.save(h);
    }
}
