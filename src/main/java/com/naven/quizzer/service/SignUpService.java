package com.naven.quizzer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.naven.quizzer.domain.SignUp;
import com.naven.quizzer.repository.SignUpRepo;

@Service
public class SignUpService {

    @Autowired
    private SignUpRepo rep;

    public void save(SignUp signUp) {
        rep.save(signUp);
    }

    public List<SignUp> getAllUserDetails() {
        return rep.findAll();

    }
    public boolean existsByUsername(String username) {
        return rep.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return rep.existsByEmail(email);
    }

    public SignUp findByUsername(String username) {
        return rep.findByUsername(username);
        
    }
}


