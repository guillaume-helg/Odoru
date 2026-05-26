package com.odoru.memberservice.repository;

import com.odoru.memberservice.model.Member;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

/** MongoDB repository for {@link Member} entities. */
public interface MemberRepository extends MongoRepository<Member, String> {

  Optional<Member> findByEmail(String email);

  Optional<Member> findByUsername(String username);
}
