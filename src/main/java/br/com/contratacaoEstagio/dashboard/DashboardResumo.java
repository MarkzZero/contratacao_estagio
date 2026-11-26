package br.com.contratacaoEstagio.dashboard;

public record DashboardResumo(
        long totalAlunos,
        long alunosAtivos,
        long totalEmpresas,
        long empresasAtivas,
        long contratosAtivos,
        long contratosVencendo) {
}
