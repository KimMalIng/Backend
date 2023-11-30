package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.UserDto;
import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //로그인은 추후 구현
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.create(userDto));
    }

    @GetMapping("/join")
    public ResponseEntity<List<User>> findAll(){
        List<User> list = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
