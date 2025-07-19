package org.koreait.admin.product.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.global.controllers.CommonController;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.annotations.ApplyCommonController;
import org.koreait.global.search.ListData;
import org.koreait.product.constants.ProductStatus;
import org.koreait.product.controllers.ProductSearch;
import org.koreait.product.entities.Product;
import org.koreait.product.services.ProductInfoService;
import org.koreait.product.services.ProductUpdateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@ApplyCommonController
@RequiredArgsConstructor
@RequestMapping("/admin/product")
public class ProductController extends CommonController {

    private final ProductUpdateService updateService;
    private final ProductInfoService infoService;
    private final FileInfoService fileInfoService;


    @Override
    @ModelAttribute("mainCode")
    public String mainCode() {
        return "product";
    }

    @ModelAttribute("addCss")
    public List<String> addCss() {
        return List.of("product/style");
    }

    @ModelAttribute("statusList")
    public ProductStatus[] statusList() {
        return ProductStatus.values();
    }

    // 상품 목록
    @GetMapping({"", "/list"})
    public String list(@ModelAttribute ProductSearch search, Model model) {
        commonProcess("list", model);

        ListData<Product> data = infoService.getList(search);
        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return "admin/product/list";
    }

    /**
     * 목록에서 상품 정보 수정과 삭제
     * @return
     */
    @RequestMapping(method={RequestMethod.PATCH, RequestMethod.DELETE})
    public String listPs(@RequestParam(name="idx", required = false) List<Integer> idxes, Model model) {

        updateService.processList(idxes);

        // 처리가 완료되면 목록을 갱신
        model.addAttribute("script", "parent.location.reload()");
        return "common/_execute_script";
    }

    // 상품 등록
    @GetMapping("/register")
    public String register(@ModelAttribute RequestProduct form, Model model) {
        commonProcess("register", model);
        form.setGid(UUID.randomUUID().toString());
        form.setStatus(ProductStatus.READY);

        return "admin/product/register";
    }

    // 상품 정보 수정
    @GetMapping("/update/{seq}")
    public String update(@PathVariable("seq") Long seq, Model model) {
        commonProcess("update", model);

        RequestProduct form = infoService.getForm(seq);
        model.addAttribute("requestProduct", form);

        return "admin/product/update";
    }

    /**
     * 상품 등록, 수정 처리
     *
     * @return
     */
    @PostMapping("/save")
    public String saveProduct(@Valid RequestProduct form, Errors errors, Model model) {
        String mode = Objects.requireNonNullElse(form.getMode(), "add");
        commonProcess(mode.equals("edit") ? "register": "update", model);

        if (errors.hasErrors()) {
            // 검증 실패시에 업로드된 파일 정보를 유지
            String gid = form.getGid();
            form.setListImages(fileInfoService.getList(gid, "list", FileStatus.ALL));
            form.setMainImages(fileInfoService.getList(gid, "main", FileStatus.ALL));
            form.setEditorImages(fileInfoService.getList(gid, "editor", FileStatus.ALL));

            return "admin/product/" + (mode.equals("edit") ? "update" : "register");
        }

        updateService.process(form);

        // 상품 등록 완료 후 상품 목록으로 이동
        return "redirect:/admin/product";
    }

    /**
     * 상품 분류 관리
     *
     * @param model
     * @return
     */
    @GetMapping("/category")
    public String category(Model model) {
        commonProcess("category", model);

        return "admin/product/category";
    }

    /**
     * 공통 처리 부분
     * @param code
     * @param model
     */
    private void commonProcess(String code, Model model) {
        code = StringUtils.hasText(code) ? code : "list";
        String pageTitle = "";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (List.of("register", "update").contains(code)) { // 상품 등록 또는 수정
            addCommonScript.add("fileManager");
            addScript.add("product/form"); // 파일 업로드 후속 처리 또는 양식 처리 관련
            pageTitle = code.equals("update") ? "상품정보 수정" : "상품등록";

        } else if (code.equals("list")) {
            pageTitle = "상품목록";
        } else if (code.equals("category")) {
            pageTitle = "분류관리";
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("subCode", code);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }
}
