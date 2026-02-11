package org.bit.bitgram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 자동 시간 기록 기능 활성화
public class BitgramApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitgramApplication.class, args);
	}

}
