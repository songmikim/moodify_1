package org.koreait.main.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.libs.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@ApplyCommonController
@RequiredArgsConstructor
public class MainController {

    private final Utils utils;

    @GetMapping
    public String index() {

        return utils.tpl("main/index");
    }
}
