package org.koreait.global.search;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@ToString
public class  Pagination {

    private int page;
    private int total;
    private int range;
    private int limit;
    private int firstRangePage;
    private int lastRangePage;
    private int prevRangePage;
    private int nextRangePage;
    private int lastPage;
    private String baseUrl;


    public Pagination(int page, int total) {
        this(page, total, 0, 0);
    }

    public Pagination(int page, int total, int range, int limit) {
        this(page, total, range, limit, null);
    }

    /**
     *
     * @param page : 페이지 번호, 1, 2, 3, 4,
     * @param total : 총 레코드 갯수
     * @param range : 페이지 구간
     * @param limit : 한 페이지당 보여줄 레코드 갯수
     */
    public Pagination(int page, int total, int range, int limit, HttpServletRequest request) {
        page = Math.max(page, 1);
        range = range < 1 ? 10 : range;
        limit = limit < 1 ? 20 : limit;

        if (total < 1) return;

        // 전체 페이지 갯수
        int totalPages = (int)Math.ceil(total / (double)limit);

        // 페이지 구간 번호 - 0, 1, 2 ...
        int rangeNum = (page - 1) / range;
        int firstRangePage = rangeNum * range + 1; // 현재 구간에서의 첫번째 페이지 번호
        int lastRangePage = firstRangePage + range - 1; // 현재 구간에서의 마지막 페이지 번호
        lastRangePage = Math.min(totalPages, lastRangePage); // 마지막 페이지는 총 페이지의 갯수 이하

        // 이전 구간의 마지막 페이지
        // 0이면 이전 구간이 없다.
        int prevRangePage = rangeNum > 0 ? firstRangePage - 1 : 0;

        // 다음 구간의 첫번째 페이지, 0 이면 다음 구간이 없다.
        int lastRangeNum = (totalPages - 1) / range; // 마지막 페이지 구간 번호
        int nextRangePage = rangeNum < lastRangeNum ? lastRangePage + 1 : 0;


        // 쿼리스트링 처리 S
        // ?page=10&post_id=freetalk&t=100&page=11....
        String qs = request == null ? "" : request.getQueryString();
        String baseUrl = "?";
        if (StringUtils.hasText(qs)) {
            baseUrl += Arrays.stream(qs.split("&"))
                    .filter(s -> !s.contains("page="))
                    .collect(Collectors.joining("&")) + "&";
        }
        baseUrl += "page=";
        // 쿼리스트링 처리 E

        this.page = page;
        this.range = range;
        this.limit = limit;
        this.total = total;
        this.lastPage = totalPages;
        this.firstRangePage = firstRangePage;
        this.lastRangePage = lastRangePage;
        this.prevRangePage = prevRangePage;
        this.nextRangePage = nextRangePage;
        this.baseUrl = baseUrl;
    }

    // ?page=번호

    /**
     * String 배열의 0번째 - 페이지 번호, 1번째 - ?page=번호와 같은 쿼리스트링
     * @return
     */
    public List<String[]> getPages() {

        return total < 1 ? List.of() : IntStream.rangeClosed(firstRangePage, lastRangePage)
                .mapToObj(i -> new String[] {"" + i, baseUrl + i})
                .toList();
    }
}
