package com.mwu.profileservice.controller;

import com.mwu.profileservice.DTO.AddressDTO;
import com.mwu.profileservice.service.AddressService;
import com.mwu.profileservice.util.AuthUtil;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;


    @PostMapping("/address")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        Long userId = authUtil.loggedInUser();
        AddressDTO createdAddress = addressService.createAddress(addressDTO, userId);
        return new ResponseEntity<>(createdAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress() {
        List<AddressDTO> addressDtoList = addressService.getAllAddresses();

        return new ResponseEntity<>(addressDtoList, HttpStatus.OK);
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDto = addressService.getAddressById(addressId);

        return new ResponseEntity<>(addressDto, HttpStatus.OK);
    }

    @GetMapping("/user/address")
    public ResponseEntity<List<AddressDTO>> getAddressesByUser() {
        Long userId = authUtil.loggedInUser();
        List<AddressDTO> addressDtoList = addressService.getAddressesByUserId(userId);

        return new ResponseEntity<>(addressDtoList, HttpStatus.OK);
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId,
                                                        @RequestBody AddressDTO addressDto) {
        AddressDTO updatedAddressDto = addressService.updateAddressById(addressId, addressDto);

        return new ResponseEntity<>(updatedAddressDto, HttpStatus.OK);
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId) {
        String status = addressService.deleteAddress(addressId);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
