package cn.caldm.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("cn.caldm.www.infra.mapper")
@MapperScan("cn.caldm.www.auth_context.infrastructure.persistence.mapper")
@ServletComponentScan(basePackages = "cn.caldm.www.auth_context.interfaces.filter")
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
