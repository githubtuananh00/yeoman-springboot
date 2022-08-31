package <%= packageName %>.web.controllers;

import <%= packageName %>.entities.<%= entityName %>;
import <%= packageName %>.entities.ResponseObj;
import <%= packageName %>.services.<%= entityName %>Service;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("<%= basePath %>")
@Slf4j
public class <%= entityName %>Controller {

    private final <%= entityName %>Service <%= entityVarName %>Service;

    @Autowired
    public <%= entityName %>Controller(<%= entityName %>Service <%= entityVarName %>Service) {
        this.<%= entityVarName %>Service = <%= entityVarName %>Service;
    }

    @GetMapping
    public ResponseEntity<ResponseObj> getAll<%= entityName %>s() {
                ;
        return ResponseEntity.status(200).body(
            new ResponseObj(true, "Get all <%= entityVarName %>s successfully",<%= entityVarName %>Service.findAll<%= entityName %>s())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObj> get<%= entityName %>ById(@PathVariable <%=fields.id%> id) {
        
        
        
        return ResponseEntity.status(200).body(new ResponseObj(true,
        "Get a <%= entityVarName %> successfully",
        <%= entityVarName %>Service
                .find<%= entityName %>ById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build())));
    }

    @PostMapping
    // @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseObj> create<%= entityName %>(@RequestBody @Validated <%= entityName %> <%= entityVarName %>) {
        return ResponseEntity.status(200).body(new ResponseObj(
            true,
            "Create a <%= entityName %> successfully",
            <%= entityVarName %>Service.save<%= entityName %>(<%= entityVarName %>)
        )); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObj> update<%= entityName %>(
            @PathVariable <%=fields.id%> id, @RequestBody <%= entityName %> <%= entityVarName %>) {
        
        
                return ResponseEntity.status(200).body(new ResponseObj(
                    true,
                    "Update a <%= entityName %> successfully",
                    <%= entityVarName %>Service
                    .find<%= entityName %>ById(id)
                    .map(
                            <%= entityVarName %>Obj -> {
                                <%= entityVarName %>.setId(id);
                                return ResponseEntity.ok(<%= entityVarName %>Service.save<%= entityName %>(<%= entityVarName %>));
                            })
                    .orElseGet(() -> ResponseEntity.notFound().build())
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObj> delete<%= entityName %>(@PathVariable <%=fields.id%> id) {
        return ResponseEntity.status(200).body(new ResponseObj(
            true,
            "Delete a <%= entityName %> successfully",
            <%= entityVarName %>Service
                .find<%= entityName %>ById(id)
                .map(
                        <%= entityVarName %> -> {
                            <%= entityVarName %>Service.delete<%= entityName %>ById(id);
                            return ResponseEntity.ok(<%= entityVarName %>);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build())
        ));
        
        
    }
}