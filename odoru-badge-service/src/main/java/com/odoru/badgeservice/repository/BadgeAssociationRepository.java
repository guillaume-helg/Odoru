package com.odoru.badgeservice.repository;

import java.util.Optional;
import com.odoru.badgeservice.model.BadgeAssociation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface managing MongoDB persistence for BadgeAssociation entities.
 */
@Repository
public interface BadgeAssociationRepository
    extends MongoRepository<BadgeAssociation, String> {

  /**
   * Finds a badge association by the member ID.
   *
   * @param memberId the unique identifier of the member
   * @return an Optional containing the association if found
   */
  Optional<BadgeAssociation> findByMemberId(String memberId);

  /**
   * Finds a badge association by the badge number.
   *
   * @param badgeNumber the unique random badge number
   * @return an Optional containing the association if found
   */
  Optional<BadgeAssociation> findByBadgeNumber(String badgeNumber);
}
