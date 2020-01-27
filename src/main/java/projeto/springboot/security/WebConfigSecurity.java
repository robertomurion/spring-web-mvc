package projeto.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override //Configura as solicitacoes de acesso do Http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		.disable() //desativa as config de padrao de memoria do Spring
		.authorizeRequests() //Permite restringir acessos
		.antMatchers(HttpMethod.GET,"/").permitAll() //Qualquer usuario acessa a pagina inicial
		.antMatchers(HttpMethod.GET,"/cadastropessoa").hasAnyRole("ADMIN")
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuario
		.and().logout()  // Mapeia URL de Logout e invalida usuario autenticado
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override  //Cria autenticacao do usuario com banco de dados ou em memoria
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		//criptografia em memoria
		/*
		 * auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		 * .withUser("admin")
		 * .password("$2a$10$o6lgbRfQG3htTGP7xgJRJOhWqQ8/M84yqIl60EuIRIOUiT56OWOKG")
		 * .roles("ADMIN");
		 */
	
	}
	
	@Override  //Ignora URLs especificas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}
}
