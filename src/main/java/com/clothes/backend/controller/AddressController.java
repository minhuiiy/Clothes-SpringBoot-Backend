package com.clothes.backend.controller;

import com.clothes.backend.dto.request.AddressRequest;
import com.clothes.backend.dto.response.AddressResponse;
import com.clothes.backend.security.services.UserDetailsImpl;
import com.clothes.backend.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final UserAddressService addressService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<AddressResponse>> getUserAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(addressService.getByUserId(userDetails.getId()));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponse> addAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.create(userDetails.getId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponse> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(userDetails.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        addressService.delete(userDetails.getId(), id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefault(userDetails.getId(), id));
    }
}
