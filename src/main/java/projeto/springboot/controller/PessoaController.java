package projeto.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import projeto.springboot.model.Pessoa;
import projeto.springboot.model.Profissao;
import projeto.springboot.model.Telefone;
import projeto.springboot.repository.PessoaRepository;
import projeto.springboot.repository.ProfissaoRepository;
import projeto.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;
	@Autowired
	private TelefoneRepository telefoneRepository;
	@Autowired
	private ReportUtil reportUtil;
	@Autowired
	private ProfissaoRepository profissaoRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		profissoes(modelAndView);
		modelAndView.addObject("pessoaObj", new Pessoa());

		return modelAndView;
	}

	private ModelAndView profissoes(ModelAndView modelAndView) {
		Iterable<Profissao> profissaoIt = profissaoRepository.findAll();
		modelAndView.addObject("profissoes", profissaoIt);
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		// validação com uso do framework
		if (bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIt);
			modelAndView.addObject("pessoaObj", pessoa);

			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // getDefaultMessage vem das anotacoes (NotEmpty, NotNull) na
															// classe Pessoa
			}
			profissoes(modelAndView);
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}

		pessoaRepository.save(pessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		profissoes(modelAndView);
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAllByOrderByIdAsc();
		profissoes(modelAndView);
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;
	}

	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaObj", pessoa.get());
		profissoes(modelAndView);
		return modelAndView;
	}

	@GetMapping("/excluirpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		pessoaRepository.deleteById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("pessoaObj", new Pessoa());
		profissoes(modelAndView);
		return modelAndView;
	}

	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo) {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		if (!pesqsexo.isEmpty()) {
			List<Pessoa> pessoasList = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
			modelAndView.addObject("pessoas", pessoasList);
		} else if (pesqsexo.isEmpty()) {
			List<Pessoa> pessoasList = pessoaRepository.findPessoaByName(nomepesquisa);
			modelAndView.addObject("pessoas", pessoasList);
		}
		modelAndView.addObject("pessoaObj", new Pessoa());
		profissoes(modelAndView);
		return modelAndView;
	}

	@GetMapping("**/pesquisarpessoa")
	public void relatorio(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesqsexo") String pesqsexo,
			HttpServletRequest request, HttpServletResponse response) {
		List<Pessoa> pessoas = new ArrayList<>();
		if (!pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		} else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		/*Chame o servico que faz a geracao do relatorio*/
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		/*Tamanho da resposta */
		response.setContentLength(pdf.length);
		/*Definir na resposta o tipo de arquivo*/
		response.setContentType("application/octet-stream");
		/*Definir cabecalho da resposta*/
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		/*Finaliza a resposta para o navegador*/
		try {
			response.getOutputStream().write(pdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		List<Telefone> telefones = telefoneRepository.findTelefoneByIdPessoa(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");

		modelAndView.addObject("pessoaObj", pessoa.get());
		modelAndView.addObject("telefones", telefones);
		return modelAndView;
	}

	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		List<Telefone> telefones = telefoneRepository.findTelefoneByIdPessoa(pessoaid);

		// validação manual sem uso do framework
		if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {

			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaObj", pessoa);
			modelAndView.addObject("telefones", telefones);

			List<String> msg = new ArrayList<>();
			if (telefone.getNumero().isEmpty()) {
				msg.add("Campo número de telefone deve ser preenchido");
			}
			if (telefone.getTipo().isEmpty()) {
				msg.add("Campo tipo de telefone deve ser preenchido");
			}
			modelAndView.addObject("msg", msg);
			return modelAndView;
		}

		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefones);
		return modelAndView;
	}

	@GetMapping("/excluirtelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();

		telefoneRepository.deleteById(idtelefone);

		List<Telefone> telefones = telefoneRepository.findTelefoneByIdPessoa(pessoa.getId());
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");

		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefones);
		return modelAndView;
	}

}
