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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.BadCredentialsException;


import <%= packageName %>.util.JwtUtil;


<%_ if (entityName==='User') { _%>
    @RestController
@RequestMapping("<%= basePath %>")
@Slf4j
public class <%= entityName %>Controller {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final <%= entityName %>Service <%= entityVarName %>Service;

    @Autowired
    private UserDetailsService userDetailsService;
    

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public <%= entityName %>Controller(<%= entityName %>Service <%= entityVarName %>Service) {
        this.<%= entityVarName %>Service = <%= entityVarName %>Service;
    }

    @GetMapping(path = "/auth")
    public ResponseEntity<ResponseObj> verifyToken(@RequestHeader(value = "authorization", defaultValue = "") String auth){
        System.out.println(auth);
        String token = auth.substring(7);
        System.out.println(auth);

        return ResponseEntity.status(200)
                .body(new ResponseObj(true,"Verify token",jwtUtil.verifyToken(token)));
    }
    
    @GetMapping(path = "/")
    public ResponseEntity<ResponseObj> getUser() {
        // return ResponseEntity.ok().body(userService.getUsers());
        return ResponseEntity.status(200)
                .body(new ResponseObj(true, "Get all users", <%= entityVarName %>Service.getUsers()));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ResponseObj> registerUser(@RequestBody <%= entityName %> <%= entityVarName %>) {
        String username = <%= entityVarName %>.getUsername();
        String password = <%= entityVarName %>.getPassword();
        User user1 = <%= entityVarName %>Service.findByUserName(username);
        if (user1 != null) {
            return ResponseEntity.status(400)
                    .body(new ResponseObj(false, "User already exists", null));
        }
        if (username.length() > 20) {
            return ResponseEntity.status(400).body(new ResponseObj(false,
                    "Max length username 20. Please re-enter username", null));
        }
        if (password.length() < 6 || password.length() > 15) {
            return ResponseEntity.status(400).body(new ResponseObj(false,
                    "Password has at least 6 characters and maximum is 15 characters . Please re-enter password",
                    null));
        }
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObj(true, "Register successfully", <%= entityVarName %>Service.saveUser(<%= entityVarName %>)));

    }
    @PostMapping(path = "/login")
    public ResponseEntity<ResponseObj> login(@RequestBody <%= entityName %> <%= entityVarName %>) throws Exception {
        String username = <%= entityVarName %>.getUsername();
        String password = <%= entityVarName %>.getPassword();
        if (username.length() * password.length() == 0)
            return ResponseEntity.status(400)
                    .body(new ResponseObj(false, "Missing username or password", null));
        User user1 = userService.findByUserName(username);

        if (user1 == null) {
            return ResponseEntity.status(400)
                    .body(new ResponseObj(false, "Incorrect Username or Password", null));
        }

        boolean passwordValid = passwordEncoder.matches(password, user1.getPassword());
        // System.out.println(passwordEncoder.matches("123456",passwordValid));
        if (!passwordValid) {
            return ResponseEntity.status(400)
                    .body(new ResponseObj(false, "Incorrect Username or Password", null));
        }

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authenticationToken);

        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect Username or Password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(<%= entityVarName %>.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);


        return ResponseEntity.status(200)
                .body(new ResponseObj(true, "Login successfully", new AuthenticateResponse(jwt)));
    }
    <%_} else { _%>
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
<%_}_%>