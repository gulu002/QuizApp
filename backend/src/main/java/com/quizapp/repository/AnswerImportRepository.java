package com.quizapp.repository;

import com.quizapp.entity.AnswerImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerImportRepository extends JpaRepository<AnswerImport, Long> {

    List<AnswerImport> findByImported(Integer imported);

    long countByImported(Integer imported);

    Optional<AnswerImport> findBySourceNumber(Integer sourceNumber);
}