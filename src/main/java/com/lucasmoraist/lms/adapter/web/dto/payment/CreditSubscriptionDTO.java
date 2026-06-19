package com.lucasmoraist.lms.adapter.web.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreditSubscriptionDTO(
        @NotBlank(message = "Holder name is required")
        String holderName,
        @NotBlank(message = "Card number is required")
        String cardNumber,
        @NotBlank(message = "Expiration month is required")
        @Size(min = 2, max = 2, message = "Expiration month must be 2 digits")
        String expMonth,
        @NotBlank(message = "Expiration year is required")
        @Size(min = 4, max = 4, message = "Expiration year must be 4 digits")
        String expYear,
        @NotBlank(message = "CVV is required")
        @Size(min = 3, max = 3, message = "CVV must be 3 digits")
        String cvv
) implements SubscriptionDTO {

}
