package com.mwu.profileservice.service;

import com.mwu.profileservice.DTO.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDto, Long userId);
    List<AddressDTO> getAllAddresses();
    AddressDTO getAddressById(Long addressId);
    List<AddressDTO> getAddressesByUserId(Long userId);
    AddressDTO updateAddressById(Long addressId, AddressDTO addressDto);
    String deleteAddress(Long addressId);
}