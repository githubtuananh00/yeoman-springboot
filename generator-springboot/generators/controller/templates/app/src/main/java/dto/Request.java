package <%= packageName %>.dto;

import lombok.Data;

@Data
public class <%= entityName %>Request {
    private Long id;
    private String text;
}
