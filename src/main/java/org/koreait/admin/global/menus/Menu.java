package org.koreait.admin.global.menus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Menu {
    private String code; // 메뉴 코드
    private String name; // 메뉴명
    private String link; // 메뉴 링크
}
