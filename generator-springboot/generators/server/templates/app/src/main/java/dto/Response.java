package <%= packageName %>.dto;

import lombok.Data;

@Data
public class <%= entityName %>Response {
    // private Long id;
    // private String text;
    <%_for(let key in fields){_%>
        <%_ if (key === 'id') continue; _%>
    
    private <%=fields[key]%> <%=key%>;
    
    <%_}_%>
}
