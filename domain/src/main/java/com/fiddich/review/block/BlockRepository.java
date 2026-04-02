package com.fiddich.review.block;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findAllByNoteIdOrderByDisplayOrderAsc(Long noteId);
}
