package com.fiddich.review.question;

import com.fiddich.review.block.Block;
import com.fiddich.review.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    @Builder
    private Question(Block block, String content, String answer) {
        this.block = block;
        this.content = content;
        this.answer = answer;
        this.status = QuestionStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = QuestionStatus.INACTIVE;
    }
}
