package co.com.pragma.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Page<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private BigDecimal sumApprovedAmount;
    private int approvedCount;
}