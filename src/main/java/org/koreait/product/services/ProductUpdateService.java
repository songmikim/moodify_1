package org.koreait.product.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.product.controllers.RequestProduct;
import org.koreait.file.services.FileUploadService;
import org.koreait.global.exceptions.script.AlertException;
import org.koreait.global.libs.Utils;
import org.koreait.product.constants.ProductStatus;
import org.koreait.product.entities.Product;
import org.koreait.product.repositories.ProductRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class ProductUpdateService {
    private final ProductRepository repository;
    private final FileUploadService uploadService;
    private final HttpServletRequest request;
    private final Utils utils;


    public Product process(RequestProduct form) {
        String mode = form.getMode();
        Long seq = form.getSeq(); // 상품 등록번호
        Product item = seq == null || seq < 1L ? new Product() : repository.findById(seq).orElseGet(Product::new);

        if (mode == null || mode.equals("add")) { // 상품 등록
            // 상품 등록시에만 추가되는 정보
            item.setGid(form.getGid());
        }

        // 공통 저장 정보
        item.setName(form.getName());
        item.setCategory(form.getCategory());
        item.setStatus(form.getStatus());
        item.setConsumerPrice(form.getConsumerPrice());
        item.setSalePrice(form.getSalePrice());
        item.setDescription(form.getDescription());


        repository.saveAndFlush(item);

        // 파일 업로드 완료 처리
        uploadService.processDone(form.getGid());

        return item;
    }

    /**
     * 상품목록 일괄 수정, 삭제 처리
     *
     * @param idxes
     */
    public void processList(List<Integer> idxes) {
        if (idxes == null || idxes.isEmpty()) {
            throw new AlertException("처리할 상품을 선택하세요.");
        }

        List<Product> items = new ArrayList<>();
        for (int idx : idxes) {
            Long seq = Long.valueOf(utils.getParam("seq_" + idx));
            Product item = repository.findById(seq).orElse(null);
            if (item == null) continue;

            if (request.getMethod().equalsIgnoreCase("DELETE")) { // 삭제
                item.setDeletedAt(LocalDateTime.now());
            } else { // 수정
                ProductStatus status = ProductStatus.valueOf(utils.getParam("status_" + idx));
                item.setStatus(status);
            }

            items.add(item);
        }

        repository.saveAllAndFlush(items);
    }
}
