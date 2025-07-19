package org.koreait.product.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.product.controllers.RequestProduct;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.search.ListData;
import org.koreait.global.search.Pagination;
import org.koreait.product.controllers.ProductSearch;
import org.koreait.product.entities.Product;
import org.koreait.product.entities.QProduct;
import org.koreait.product.exceptions.ProductNotFoundException;
import org.koreait.product.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Lazy
@Service
@RequiredArgsConstructor
public class ProductInfoService {

    private final ModelMapper modelMapper;
    private final ProductRepository repository;
    private final FileInfoService fileInfoService;
    private final HttpServletRequest request;

    public Product get(Long seq) {

        Product item = repository.findById(seq).orElseThrow(ProductNotFoundException::new);
        addInfo(item); // 추가 정보 처리

        return item;
    }

    public RequestProduct getForm(Long seq) {
        Product item = get(seq);
        return modelMapper.map(item, RequestProduct.class);
    }

    public ListData<Product> getList(ProductSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 20 : limit;

        String sopt = search.getSopt();
        String skey = search.getSkey();

        LocalDate sDate = search.getSDate();
        LocalDate eDate = search.getEDate();

        QProduct product = QProduct.product;

        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(product.deletedAt.isNull());

        /* 상품 등록일자 검색 처리 S */
        if (sDate != null) {
            andBuilder.and(product.createdAt.goe(sDate.atStartOfDay()));
        }

        if (eDate != null) {
            andBuilder.and(product.createdAt.loe(eDate.atTime(23, 59, 59)));
        }
        /* 상품 등록일자 검색 처리 E */

        /* 키워드 검색 처리 S */
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        if (StringUtils.hasText(skey)) {
            skey = skey.trim();
            StringExpression fields = null;
            if (sopt.equals("NAME")) {
                fields = product.name;
            } else if (sopt.equals("DESCRIPTION")) {
                fields = product.description;
            } else {
                fields = product.name.concat(product.description);
            }

            andBuilder.and(fields.contains(skey));
        }
        /* 키워드 검색 처리 E */

        // 판매가, 소비자가 검색 처리 S
        Integer sPrice = search.getSPrice();
        Integer ePrice = search.getEPrice();
        if (sPrice != null) {
            BooleanBuilder orBuilder = new BooleanBuilder();
            andBuilder.and(orBuilder.or(product.salePrice.goe(sPrice))
                    .or(product.consumerPrice.goe(sPrice)));
        }

        if (ePrice != null) {
            BooleanBuilder orBuilder = new BooleanBuilder();
            andBuilder.and(orBuilder.or(product.salePrice.loe(ePrice))
                    .or(product.consumerPrice.loe(ePrice)));
        }
        // 판매가, 소비자가 검색 처리 E

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<Product> data = repository.findAll(andBuilder, pageable);
        List<Product> items = data.getContent();
        long total = data.getTotalElements();

        items.forEach(this::addInfo); // 추가 정보 처리

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 상품 추가 정보 처리
     *
     * @param item
     */
    private void addInfo(Product item) {
        /* 업로드한 파일 처리 S */
        String gid = item.getGid();
        item.setMainImages(fileInfoService.getList(gid, "main"));
        item.setListImages(fileInfoService.getList(gid, "list"));
        item.setEditorImages(fileInfoService.getList(gid, "editor"));
        /* 업로드한 파일 처리 E */
    }
}
