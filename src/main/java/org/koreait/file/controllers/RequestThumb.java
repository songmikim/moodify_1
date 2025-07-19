package org.koreait.file.controllers;

import lombok.Data;

@Data
public class RequestThumb {
    private Long seq; // 파일 등록번호
    private int width;
    private int height;
    private boolean crop; // 크롭 여부
    private String url; // 원격 이미지 URL
}

