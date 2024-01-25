package com.api.gestion.service.impl;

import com.api.gestion.constantes.FacturaConstantes;
import com.api.gestion.dao.UserDao;
import com.api.gestion.pojo.User;
import com.api.gestion.security.CustomerDetailsService;
import com.api.gestion.security.jwt.JwtUtil;
import com.api.gestion.service.UserService;
import com.api.gestion.util.FacturaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> resquestMap) {

        log.info("Registro interno de un usuario {}" + resquestMap);

        try {
            if (validateSignUpMap(resquestMap)) {

                User user = userDao.findByEmail(resquestMap.get("email"));
                if (Objects.isNull(user)) {

                    userDao.save(getUserFromMap(resquestMap));
                    return FacturaUtils.getResponseEntity("Usuario registrado con exito", HttpStatus.CREATED);
                } else {
                    return FacturaUtils.getResponseEntity("El usuario con ese email ya existe",
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> resquestMap) {
        log.info("Dentro de login");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(resquestMap.get("email"), resquestMap.get("password"))
            );

            if (authentication.isAuthenticated()) {
                if (customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return  new ResponseEntity<String>("{\"mensjae\":\"" + jwtUtil
                            .generateToken(customerDetailsService.getUserDetail().getEmail(),
                                    customerDetailsService.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"mensaje\":\""
                            + " espere la aprobación del administrador " + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
            log.error("{}", e);
        }
        return new ResponseEntity<String>("{\"mensaje\":\"" + "Credenciales Incorrectas " + "\"}",
                HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUpMap(Map<String, String> resquestMap) {

        return resquestMap.containsKey("nombre") && resquestMap.containsKey("numeroDeContacto")
                && resquestMap.containsKey("email") && resquestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {

        User user = new User();

        user.setNombre(requestMap.get("nombre"));
        user.setNumeroDeContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;

    }
}
