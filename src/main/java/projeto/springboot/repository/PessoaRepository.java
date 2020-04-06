package projeto.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import projeto.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {


	@Query("select p from Pessoa p where p.nome like %?1% order by id")
	List<Pessoa> findByNomeIgnoreCase(String nome);
	
	@Query("select p from Pessoa p order by id")
	List<Pessoa> findAll();
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexopessoa = ?2 order by id")
	List<Pessoa> findPessoaByNameSexo(String nome, String pesqsexo);
	
	default Page<Pessoa> findByNamePage(String nome, Pageable pageable){
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		/*configurar pesquisa para consultar por parte do nome no BD*/
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		/*une o objeto com o valor e a configuraçao para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pages = findAll(example, pageable);
		return pages;
	}
	
	default Page<Pessoa> findByNameSexoPage(String nome, String sexo, Pageable pageable){
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		pessoa.setSexopessoa(sexo);
		/*configurar pesquisa para consultar por parte do nome e sexo no BD*/
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll()
				.withMatcher("sexopessoa",ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		/*une o objeto com o valor e a configuraçao para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pages = findAll(example, pageable);
		return pages;
	}
}
