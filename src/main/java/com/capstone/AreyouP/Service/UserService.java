package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.UserDto;
import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(UserDto userDto){
        User user = User.builder()
                .userId(userDto.getUserId())
                .userPw(userDto.getUserPw())
                .name(userDto.getName())
                .build();
        userRepository.save(user);

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
