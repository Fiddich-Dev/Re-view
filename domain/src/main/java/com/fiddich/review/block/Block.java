package com.fiddich.review.block;

import com.fiddich.review.common.BaseEntity;
import com.fiddich.review.note.Note;
import com.fiddich.review.question.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "blocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 텍스트 내용 또는 이미지 URL

    @Enumerated(EnumType.STRING)
    private ProblemType problemType; // null 허용 (문제 미입력 시)

    @Column(columnDefinition = "TEXT")
    private String problemContent; // 사용자가 직접 입력한 문제 (텍스트 또는 이미지 URL)

    private int displayOrder;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public List<Question> getQuestions() {
        return Collections.unmodifiableList(questions);
    }

    @Builder
    private Block(Note note, String title, ContentType contentType, String content,
                  ProblemType problemType, String problemContent, int displayOrder) {
        this.note = note;
        this.title = title;
        this.contentType = contentType;
        this.content = content;
        this.problemType = problemType;
        this.problemContent = problemContent;
        this.displayOrder = displayOrder;
    }

    public void update(String title, ContentType contentType, String content,
                       ProblemType problemType, String problemContent, int displayOrder) {
        this.title = title;
        this.contentType = contentType;
        this.content = content;
        this.problemType = problemType;
        this.problemContent = problemContent;
        this.displayOrder = displayOrder;
    }
}
