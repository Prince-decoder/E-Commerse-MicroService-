package com.ashu.UserService.Model.Address.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String Street;
    private String City;
    private String State;
    private String country;
}
