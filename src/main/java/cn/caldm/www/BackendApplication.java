package cn.caldm.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("cn.caldm.www.file_context.infrastructure.persistence.mapper")
@MapperScan("cn.caldm.www.post_context.infrastructure.persistence.mapper")
@MapperScan("cn.caldm.www.system_context.infrastructure.persistence.mapper")
@MapperScan("cn.caldm.www.user_context.infrastructure.persistence.mapper")

public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
