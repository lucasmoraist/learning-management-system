package com.lucasmoraist.lms.adapter.web.dto.payment;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = CreditSubscriptionDTO.class)
@JsonDeserialize(as = CreditSubscriptionDTO.class)
public sealed interface SubscriptionDTO permits CreditSubscriptionDTO {

}
