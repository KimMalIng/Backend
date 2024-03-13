package com.example.areyoup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
//테스트 시작 전에 트랜잭션, 완료 후 롤백
class AreyoupApplicationTests {

	@Test
	void contextLoads() {
	}

}
