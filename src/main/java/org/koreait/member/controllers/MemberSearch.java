package org.koreait.member.controllers;

import lombok.Data;
import org.koreait.global.search.CommonSearch;
import org.koreait.member.constants.Authority;

import java.util.List;

@Data
public class MemberSearch extends CommonSearch {
    private List<Authority> authority;
}
