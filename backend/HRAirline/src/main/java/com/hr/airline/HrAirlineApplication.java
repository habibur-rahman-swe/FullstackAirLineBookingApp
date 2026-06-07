package com.hr.airline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HrAirlineApplication {

	@Autowired
	private JavaMailSender javaMailSender;

	public static void main(String[] args) {
		SpringApplication.run(HrAirlineApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner() {
//		return args -> {
//			try {
//
//				MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
//						MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
//				helper.setTo("habibur@i2gether.com");
//				helper.setSubject("Hello Testing");
//				helper.setText("testing email 123, hello world");
//
//				System.out.println("About to send Email...");
//				javaMailSender.send(mimeMessage);
//
//				System.out.println("Email Sent!");
//			} catch (Exception ex) {
//				System.out.println(ex.getMessage());
//			}
//		};
//	}
}
