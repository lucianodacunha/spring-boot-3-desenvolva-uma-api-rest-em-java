package io.github.lucianodacunha.api.model;

import io.github.lucianodacunha.api.entity.Medico;

public record DadosListagemMedico(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade) {
    public DadosListagemMedico(Medico medico){
        this(medico.getId(), medico.getNome(), medico.getEmail(),
                medico.getCrm(), medico.getEspecialidade());
    }
}
