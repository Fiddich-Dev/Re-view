package com.fiddich.review.block;

import com.fiddich.review.common.exception.BusinessException;
import com.fiddich.review.note.Note;
import com.fiddich.review.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    public static final String ERR_BLOCK_NOT_FOUND = "존재하지 않는 블럭입니다.";

    private final BlockRepository blockRepository;
    private final NoteService noteService;

    public Block findById(Long blockId) {
        return blockRepository.findById(blockId)
                .orElseThrow(() -> new BusinessException(ERR_BLOCK_NOT_FOUND));
    }

    public List<Block> findAllByNote(Long noteId) {
        return blockRepository.findAllByNoteIdOrderByDisplayOrderAsc(noteId);
    }

    @Transactional
    public Block create(Long noteId, String title, ContentType contentType, String content,
                        ProblemType problemType, String problemContent) {
        Note note = noteService.findById(noteId);
        int order = blockRepository.findAllByNoteIdOrderByDisplayOrderAsc(noteId).size();
        return blockRepository.save(Block.builder()
                .note(note)
                .title(title)
                .contentType(contentType)
                .content(content)
                .problemType(problemType)
                .problemContent(problemContent)
                .displayOrder(order)
                .build());
    }

    @Transactional
    public Block update(Long blockId, String title, ContentType contentType, String content,
                        ProblemType problemType, String problemContent, int displayOrder) {
        Block block = findById(blockId);
        block.update(title, contentType, content, problemType, problemContent, displayOrder);
        return block;
    }

    @Transactional
    public void delete(Long blockId) {
        blockRepository.deleteById(blockId);
    }
}
