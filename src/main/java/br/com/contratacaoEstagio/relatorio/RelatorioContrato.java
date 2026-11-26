package br.com.contratacaoEstagio.relatorio;

import java.time.LocalDate;

public record RelatorioContrato(
        Long id,
        String ra,
        String aluno,
        String curso,
        String empresa,
        String cargo,
        String modalidade,
        LocalDate dataInicio,
        LocalDate dataTermino,
        String situacao) {
}
