package org.koreait.product.controllers;

import lombok.Data;
import org.koreait.global.search.CommonSearch;

@Data
public class ProductSearch extends CommonSearch {
    private Integer sPrice;
    private Integer ePrice;
}
