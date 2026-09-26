package com.ashu.UserService.Model.User.DTO;

import com.ashu.UserService.Model.Address.DTO.AddressRequest;
import com.ashu.UserService.Model.User.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String firstName;
    private String midName;
    private String lastName;
    private String email;
    private String password;
    private UserAuthority role;
    private AddressRequest address;
}
