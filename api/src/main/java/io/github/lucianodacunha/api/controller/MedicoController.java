package io.github.lucianodacunha.api.controller;

import io.github.lucianodacunha.api.entity.Medico;
import io.github.lucianodacunha.api.model.DadosCadastroMedico;
import io.github.lucianodacunha.api.model.DadosListagemMedico;
import io.github.lucianodacunha.api.model.DadosAtualizacaoMedico;
import io.github.lucianodacunha.api.repository.MedicoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/medicos")
public class MedicoController {
    @Autowired
    private MedicoRepository repository;
    @PostMapping
    @Transactional
    public void cadastrar(@RequestBody @Valid DadosCadastroMedico dados){
        repository.save(new Medico(dados));
    }

    /**
     * Para fazer paginação, devemos retornar um objeto Page.
     * O repository tem uma sobrecarga do método find que retorna um Page, e
     * como parâmetro, recebe um objeto Pageable, além da anotação
     * @PageableDefault, que define a quantidade de registros por página e a
     * ordenação.
     * @param paginacao
     * @return
     */
    @GetMapping
    public Page<DadosListagemMedico> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        return repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    }

    @PutMapping
    @Transactional
    public void atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
        var medico = repository.getReferenceById(dados.id());
        medico.atualizarInformacoes(dados);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void excluir(@PathVariable Long id){
        var medico = repository.getReferenceById(id);
        repository.delete(medico);
    }
}

