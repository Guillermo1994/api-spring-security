package com.api.gestion.rest;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.service.UserService;
import com.api.gestion.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsario(@RequestBody(required = true) Map<String, String> resquestMap) {

        try {
            return userService.signUp(resquestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap) {
//        System.out.println("------------------" + requestMap.get("email"));
//        System.out.println(requestMap.get("password"));
        try {
            return userService.login(requestMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
