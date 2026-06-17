package com.odoru.competitionservice.repository;

import com.odoru.competitionservice.model.Competition;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface CompetitionRepository
    extends MongoRepository<Competition, String>
{
    List<Competition> findByTargetLevel(int targetLevel);

    List<Competition> findByTeacherId(String teacherId);
}
