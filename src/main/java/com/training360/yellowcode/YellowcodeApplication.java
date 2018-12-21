package com.training360.yellowcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@SpringBootApplication
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class YellowcodeApplication extends
		WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(YellowcodeApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/", "/js/**", "/css/**", "/api/products").permitAll()
				.antMatchers("/adminproducts.html", "/users.html", "/orders.html", "/orderitems.html","/reports.html", "/categories.html","/dashboard.html").hasRole("ADMIN")
				.antMatchers("/basket.html", "/myorders.html", "/myorderitem.html","/profile.html").authenticated()
				.and()
				.formLogin().loginPage("/login.html").defaultSuccessUrl("/products.html")
				.and()
				.logout();

	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, DataSource dataSource, PasswordEncoder passwordEncoder) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder)
				.usersByUsernameQuery("select user_name, password, enabled from users where user_name=?")
				.authoritiesByUsernameQuery("select user_name, role from users where user_name=?");

	}
}