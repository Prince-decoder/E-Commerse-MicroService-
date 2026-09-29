package com.ashu.CartService.Feing;

import com.ashu.CartService.Model.User.DTO.UserRequest;
//import com.ashu.CartService.Model.User.DTO.UserResponse;
import com.ashu.CartService.Model.User.UserDetail.UserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FeignClient("USERSERVICE")
public interface UserFeing  {

    @PostMapping("api/add")
    public Boolean addUser(@RequestBody UserRequest userRequest) ;

//    @GetMapping("api/users")
//    public List<UserResponse> allUsers();

    @GetMapping("api/find")
    public Optional<UserDetails> getUserId(@RequestParam String userId);

    @PostMapping("update")
    public boolean userUpdated(@RequestBody UserRequest userRequest);
}
