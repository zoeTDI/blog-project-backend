package cn.caldm.www.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;

/**
 * Persistence-agnostic page result shared by application boundaries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;
    private long pages;

    public <R> PageResult<R> map(Function<T, R> mapper) {
        return new PageResult<>(records.stream().map(mapper).toList(), total, page, size, pages);
    }
}
