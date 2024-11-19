package com.netty100.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netty100.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author why
 */
@Slf4j
@Component
public class Jwts {
    /**
     * 认证token 键
     */
    public static final String HEADER = "Authorization";

    /**
     * 加密值,也用于生成AES对象的key
     */
    private static final String SECRET = "QKvk+XyIOCdzBt9DNtU/HA==";

    /**
     * 设置JWT初始信息
     */
    private static final Map<String, Object> JWT_HEADER = new HashMap<>(4);

    static {
        JWT_HEADER.put("typ", "JWT");
        JWT_HEADER.put("alg", "HS256");
    }

    /**
     * 加密用户id
     */
    private static final String ID_KEY = "id";
    /**
     * 加密用户信息
     */
    private static final String USER_KEY = "user";

    /**
     * token有效时间,7天
     */
    @Setter
    @Getter
    @Value(value = "${netty100.auth.token.invalid-mills}")
    private Long invalidMills;

    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private static final SymmetricCrypto AES = new SymmetricCrypto(SymmetricAlgorithm.AES, DECODER.decode(SECRET));

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String create(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            user.setPassword(null);
            String result = JWT.create()
                    .withHeader(JWT_HEADER)
                    .withExpiresAt(new Date(DateUtil.current() + invalidMills))
                    .withClaim(ID_KEY, user.getId())
                    .withClaim(USER_KEY, objectMapper.writeValueAsString(user))
                    .sign(algorithm);
            return AES.encryptHex(result, StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            log.error("生成token异常", e);
            return null;
        }
    }


    public boolean verify(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            token = AES.decryptStr(token.trim(), StandardCharsets.UTF_8);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("校验token出现异常", e);
            return false;
        }

    }


    public Integer getUserId(String token) {
        if (!StringUtils.hasText(token)) {
            return 0;
        }
        try {
            token = AES.decryptStr(token.trim(), StandardCharsets.UTF_8);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim(ID_KEY).asInt();
        } catch (Exception e) {
            log.error("解析token获取userId出现异常", e);
            return 0;
        }
    }


    public User getUser(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            token = AES.decryptStr(token.trim(), StandardCharsets.UTF_8);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return objectMapper.readValue(jwt.getClaim(USER_KEY).asString(), User.class);
        } catch (Exception e) {
            log.error("解析token获取用户信息出现异常", e);
            return null;
        }
    }
}
