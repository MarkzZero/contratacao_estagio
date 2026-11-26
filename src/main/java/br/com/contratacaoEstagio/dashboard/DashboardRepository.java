package br.com.contratacaoEstagio.dashboard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardResumo buscarResumo(int diasAlerta) {
        return jdbcTemplate.queryForObject(
                "CALL sp_resumo_dashboard(?)",
                (rs, i) -> new DashboardResumo(
                        rs.getLong("total_alunos"),
                        rs.getLong("alunos_ativos"),
                        rs.getLong("total_empresas"),
                        rs.getLong("empresas_ativas"),
                        rs.getLong("contratos_ativos"),
                        rs.getLong("contratos_vencendo")),
                diasAlerta);
    }

    @Transactional
    public int encerrarContratosVencidos() {
        Integer total = jdbcTemplate.queryForObject(
                "CALL sp_encerrar_contratos_vencidos()",
                (rs, i) -> rs.getInt("encerrados"));
        return total == null ? 0 : total;
    }
}
