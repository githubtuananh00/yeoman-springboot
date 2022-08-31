package <%= packageName %>.dto;

import lombok.Data;

@Data
public class <%= entityName %>Response {
    private Long id;
    private String text;
    
}
