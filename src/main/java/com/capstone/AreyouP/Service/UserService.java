package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.UserDto;
import com.capstone.AreyouP.Domain.ProfileImage;
import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Exception.UserDuplicateException;
import com.capstone.AreyouP.Repository.ProfileImageRepository;
import com.capstone.AreyouP.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    public User create(UserDto userDto) throws IOException {
        if (userRepository.findByUserId(userDto.getUserId()).isPresent()){
            throw new UserDuplicateException("아이디가 이미 존재합니다");
        }
        ProfileImage image = new ProfileImage();
        if (userDto.getImage()!=null) {
            MultipartFile File = userDto.getImage();
            image.setData(File.getBytes());
            profileImageRepository.save(image); //이미지 저장
        }
        User user = User.builder()
                .userId(userDto.getUserId())
                .userPw(userDto.getUserPw())
                .name(userDto.getName())
                .University(userDto.getUniversity())
                .major(userDto.getMajor())
                .nickname(userDto.getNickname())
                .profileImg(image)
                .build();
        userRepository.save(user);

        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Object login(UserDto userDto) {
        if (userRepository.findByUserId(userDto.getUserId()).isPresent()){
            throw new UserDuplicateException("아이디가 이미 존재합니다");
        }

        String userId = userDto.getUserId();
        String userPw = userDto.getUserPw();

        return null;
    }
}
