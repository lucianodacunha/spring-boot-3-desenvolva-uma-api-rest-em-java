package io.github.lucianodacunha.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoPaciente(
        @NotNull Long id,
        String nome,
        String telefone,
        DadosEndereco endereco) {}
