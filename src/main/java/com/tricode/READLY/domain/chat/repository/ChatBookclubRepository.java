package com.tricode.READLY.domain.chat.repository;

import com.tricode.READLY.domain.chat.entity.ChatBookclub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatBookclubRepository extends JpaRepository<ChatBookclub, Long> {

    @Query("select cbc from ChatBookclub cbc join fetch cbc.chat where cbc.bookClub.id = :clubId")
    List<ChatBookclub> findAllByBookClubIdWithChat(@Param("clubId") Long clubId);
}
