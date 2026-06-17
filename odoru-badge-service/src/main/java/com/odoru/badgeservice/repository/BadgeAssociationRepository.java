package com.odoru.badgeservice.repository;

import com.odoru.badgeservice.model.BadgeAssociation;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeAssociationRepository
    extends MongoRepository<BadgeAssociation, String>
{
    Optional<BadgeAssociation> findByMemberId(String memberId);

    Optional<BadgeAssociation> findByBadgeNumber(String badgeNumber);
}
