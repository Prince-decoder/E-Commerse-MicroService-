package com.ashu.UserService.Controller;

import com.ashu.UserService.Model.User.DTO.UserRequest;
import com.ashu.UserService.Model.User.DTO.UserResponse;
import com.ashu.UserService.Model.User.UserDetails;
import com.ashu.UserService.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("add")
    public ResponseEntity<String> addUser(@RequestBody UserRequest userRequest)
    {
        if(userService.addUser(userRequest))
        {
            return ResponseEntity.ok("User Added");
        }
        return ResponseEntity.badRequest().body("Not able to add User");
    }

    @GetMapping("users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        if(userService.allUsers()==null)
        {
            return ResponseEntity.notFound().build();
        }
        else
        {
            return ResponseEntity.ok(userService.allUsers());
        }
    }
    @GetMapping("find")
    public ResponseEntity<UserDetails> getUserById(@RequestParam String userId)
    {
        return userService.getUserId(userId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("update")
    public ResponseEntity<String> updateUser(@RequestParam String userId, @RequestBody UserRequest request)
    {
        if(userService.userUpdated(request))
        {
            return ResponseEntity.ok("User Updated");
        }
        else
        {
            return ResponseEntity.noContent().build();
        }
    }
}
