package com.ashu.UserService.Security;

import com.ashu.UserService.Repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ImpUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.ashu.UserService.Model.User.UserDetails user= userRepository.findByName(username);
        if(user==null) {
            System.out.println("user not found");
            throw new UsernameNotFoundException("user not found");
        }
        else
        {
            return new ImpUserDetails(user);
        }
    }
}