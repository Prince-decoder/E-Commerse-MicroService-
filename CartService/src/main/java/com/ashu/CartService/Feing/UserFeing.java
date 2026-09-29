package com.ashu.CartService.Feing;

import com.ashu.CartService.Model.User.UserDetail.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient("USERSERVICE")
public interface UserFeing  {


    @GetMapping("api/find")
    public Optional<UserDetails> getUserId(@RequestParam String userId);

    @GetMapping("api/username")
    public ResponseEntity<UserDetails> getUserByUsername(@RequestParam String username);
}
