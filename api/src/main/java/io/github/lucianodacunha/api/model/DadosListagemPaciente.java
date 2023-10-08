package io.github.lucianodacunha.api.model;


import io.github.lucianodacunha.api.entity.Paciente;

/**
 * DTO criado para filtrar somente as propriedades desejadas, evitando assim,
 * trafegar e exibir informações desnecessárias para a app.
 *
 */
public record DadosListagemPaciente(
        Long id,
        String nome,
        String email,
        String cpf){
    public DadosListagemPaciente(Paciente paciente){
        this(paciente.getId(), paciente.getNome(), paciente.getEmail(),
            paciente.getCpf());
    }
}

