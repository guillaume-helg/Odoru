package com.odoru.badgeservice.repository;

import java.util.Optional;
import com.odoru.badgeservice.model.BadgeAssociation;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link BadgeAssociation} entities.
 */
public interface BadgeAssociationRepository
    extends MongoRepository<BadgeAssociation, String> {

  Optional<BadgeAssociation> findByMemberId(String memberId);

  Optional<BadgeAssociation> findByBadgeNumber(String badgeNumber);
}
