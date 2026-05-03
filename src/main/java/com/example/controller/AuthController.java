package com.example.controller;

import com.example.dto.JwtResponse;
import com.example.dto.LoginRequest;
import com.example.model.auth.UserDetailsImpl;
import com.example.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    System.out.println("--- TENTATIVO DI LOGIN ---");
    System.out.println("User: " + loginRequest.getUsername());

    try {
        // 1. Autenticazione
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Setting del contesto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generazione Token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Recupero dettagli
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        System.out.println("--- LOGIN OK PER " + loginRequest.getUsername() + " ---");

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));

    } catch (Exception e) {
        System.out.println("--- ERRORE DURANTE IL LOGIN: " + e.getMessage() + " ---");
        return ResponseEntity.status(401).body("Errore: Credenziali non valide o utente non trovato");
    }
}
}