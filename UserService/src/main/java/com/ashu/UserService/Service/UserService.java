package com.ashu.UserService.Service;

import com.ashu.UserService.Model.Address.Address;
import com.ashu.UserService.Model.Address.DTO.AddressRequest;
import com.ashu.UserService.Model.Address.DTO.AddressResponse;
import com.ashu.UserService.Model.User.DTO.UserRequest;
import com.ashu.UserService.Model.User.DTO.UserResponse;
import com.ashu.UserService.Model.User.UserDetails;
import com.ashu.UserService.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public Boolean addUser(UserRequest userRequest) {
        UserDetails userDetails = new UserDetails();
        requestToUserDetails(userDetails, userRequest);
        if (userDetails.getName() != null) {
            userRepo.save(userDetails);
            return true;
        }
        return false;
    }

    public List<UserResponse> allUsers() {
        return userRepo.findAll().stream()
                .map(this::userDetailsToUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<UserDetails> getUserId(String id)
    {
        return userRepo.findById(Long.valueOf(id));
    }

    public boolean userUpdated(UserRequest userRequest)
    {
        if(userRepo.findByEmail(userRequest.getEmail())==null)
        {
            return false;
        }
        UserDetails userDetails = new UserDetails();
        requestToUserDetails(userDetails, userRequest);
        if (userDetails.getName() != null) {
            userRepo.save(userDetails);
            return true;
        }
        return false;
    }

    private UserResponse userDetailsToUserResponse(UserDetails userDetails)
    {
        UserResponse userResponse = new UserResponse();
        userResponse.setName(userDetails.getName());
        userResponse.setEmail(userDetails.getEmail());
        AddressResponse address = new AddressResponse();
        addressToAddressResponse(userDetails.getAddress(),address);
        userResponse.setAddress(address);
        return userResponse;
    }

    private void addressToAddressResponse(Address address, AddressResponse address1) {
        address1.setCity(address.getCity());
        address1.setCountry(address.getCountry());
        address1.setStreet(address.getStreet());
        address1.setState(address.getState());
    }

    private void requestToUserDetails(UserDetails userDetails,UserRequest userRequest) {

        if(userRequest==null)
        {
            return;
        }
        userDetails.setName(userRequest.getFirstName()+ " "+userRequest.getMidName() +" "+userRequest.getLastName());
        userDetails.setEmail(userRequest.getEmail());
        userDetails.setPassword(userRequest.getPassword());
        if(userRequest.getRole()!=null)
        {
            userDetails.setRole(userRequest.getRole());
        }
        Address address=new Address();
        requestToAddress(userRequest.getAddress(),address);
        userDetails.setAddress(address);
    }

    private void requestToAddress(AddressRequest request, Address address) {
        if(request==null)
        {
            return;
        }
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setZipCode(request.getZipCode());
    }
}
