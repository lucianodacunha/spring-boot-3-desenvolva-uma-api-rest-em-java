package io.github.lucianodacunha.api.controller;

import io.github.lucianodacunha.api.entity.Paciente;
import io.github.lucianodacunha.api.model.*;
import io.github.lucianodacunha.api.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Anotação que indica ao Spring para carrega-la durante a inicialização
 * do projeto.
 */
@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    /**
     * Indica ao Spring que ele deve instanciar esse objeto.
     */
    @Autowired
    private PacienteRepository repository;

    /**
     * RequestBody é a anotação que indica que o Spring deve puxar do corpo da
     * requisição o conteúdo do parâmetro {dados}.
     * @param dados
     */
    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroPaciente dados){
        /**
         * Com o autowired, nesse momento, esse objeto já existe no Spring.
         */
        repository.save(new Paciente(dados));
    }

    @GetMapping
    public Page<DadosListagemPaciente> listar(
            @PageableDefault(size = 10, sort = {"nome"}) Pageable pagina){
        return repository.findAllByAtivoTrue(pagina).map(DadosListagemPaciente::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoPaciente dados) {
        var paciente = repository.getReferenceById(dados.id());
        paciente.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id){
        Paciente paciente = repository.getReferenceById(id);
        paciente.excluir();
    }
}
