package com.odoru.memberservice.repository;

import com.odoru.memberservice.model.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepository extends MongoRepository<Member, String> {

  Optional<Member> findByEmail(String email);

  Optional<Member> findByUsername(String username);

  List<Member> findByFeePaid(boolean feePaid);

  List<Member> findByMedicalCertificateProvided(
      boolean medicalCertificateProvided);

  List<Member> findByRegistrationValidated(
      boolean registrationValidated);
}
