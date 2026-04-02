package com.fiddich.review.note;

import com.fiddich.review.user.User;
import com.fiddich.review.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    public Note findById(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노트입니다."));
    }

    public List<Note> findAllByUser(Long userId) {
        return noteRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Note create(Long userId, String title) {
        User user = userService.findById(userId);
        return noteRepository.save(Note.builder()
                .user(user)
                .title(title)
                .build());
    }

    @Transactional
    public Note updateTitle(Long noteId, String title) {
        Note note = findById(noteId);
        note.updateTitle(title);
        return note;
    }

    @Transactional
    public void delete(Long noteId) {
        noteRepository.deleteById(noteId);
    }
}
