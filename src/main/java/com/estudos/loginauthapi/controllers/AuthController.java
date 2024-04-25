package com.estudos.loginauthapi.controllers;

import com.estudos.loginauthapi.domain.user.User;
import com.estudos.loginauthapi.dto.LoginRequestDTO;
import com.estudos.loginauthapi.dto.RegisterRequestDTO;
import com.estudos.loginauthapi.dto.ResponseDTO;
import com.estudos.loginauthapi.infra.security.TokenService;
import com.estudos.loginauthapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO loginRequestDTO){
        User user = this.userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if(this.passwordEncoder.matches(user.getPassword(), loginRequestDTO.password())){
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO registerRequestDTO){
        Optional<User> user = this.userRepository.findByEmail(registerRequestDTO.email());

        if(user.isEmpty()){
            User newUser = new User();
            newUser.setPassword(this.passwordEncoder.encode(registerRequestDTO.password()));
            newUser.setEmail(registerRequestDTO.email());
            newUser.setName(registerRequestDTO.name());
            this.userRepository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
        }
        return ResponseEntity.badRequest().build();
    }
}
