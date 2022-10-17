package com.john.springbootmall.service;

import com.john.springbootmall.dto.UserRegisterRequest;
import com.john.springbootmall.model.User;

public interface UserService {
    Integer register(UserRegisterRequest userRegisterRequest);

    User getUserById(Integer userId);
}
