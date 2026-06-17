package com.odoru.badgeservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "badge_associations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeAssociation {

    @Id
    private String id;

    @NotBlank(message = "Member ID is required")
    @Indexed(unique = true)
    private String memberId;

    @NotBlank(message = "Badge number is required")
    @Indexed(unique = true)
    private String badgeNumber;
}
