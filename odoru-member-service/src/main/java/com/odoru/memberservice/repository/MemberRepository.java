package com.odoru.memberservice.repository;

import java.util.Optional;
import com.odoru.memberservice.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Member entities in MongoDB.
 */
@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

  /**
   * Finds a member by their email address.
   *
   * @param email the email to search for
   * @return an Optional containing the member if found
   */
  Optional<Member> findByEmail(String email);

  /**
   * Finds a member by their username.
   *
   * @param username the username to search for
   * @return an Optional containing the member if found
   */
  Optional<Member> findByUsername(String username);
}
