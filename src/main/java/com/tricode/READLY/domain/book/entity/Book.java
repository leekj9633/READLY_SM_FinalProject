package com.tricode.READLY.domain.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String writer;
    private String coverImageUrl;   // 이미지를 S3, Supabase Storage, Firebase Storage 등에 저장하고 URL만 DB에 저장하는 방식

    // 외부 도서 API에서 가져와 저장할 추가 속성들
    private Integer pageCount;
    private Double width;
    private Double height;

    @OneToMany(mappedBy = "book")
    private List<MemberBook> memberBooks = new ArrayList<>();
}
