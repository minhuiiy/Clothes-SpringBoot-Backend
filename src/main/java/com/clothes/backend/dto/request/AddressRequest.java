package com.clothes.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Recipient name cannot be blank")
    private String recipientName;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    @NotBlank(message = "Province cannot be blank")
    private String province;

    @NotBlank(message = "District cannot be blank")
    private String district;

    @NotBlank(message = "Ward cannot be blank")
    private String ward;

    @NotBlank(message = "Address detail cannot be blank")
    private String addressDetail;

    private boolean isDefault;
}
