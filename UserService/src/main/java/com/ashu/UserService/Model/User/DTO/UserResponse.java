package com.ashu.UserService.Model.User.DTO;

import com.ashu.UserService.Model.Address.DTO.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
    private AddressResponse address;
}
