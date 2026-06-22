package cn.deru.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchReplaceRequest {
    private List<Long> ids;
    private String field;
    private String searchValue;
    private String replaceValue;
}