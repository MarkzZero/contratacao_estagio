package br.com.contratacaoEstagio.relatorio;

import java.sql.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RelatorioRepository {

    private final JdbcTemplate jdbcTemplate;

    public RelatorioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RelatorioContrato> buscarContratos(String situacao, long empresaId) {
        return jdbcTemplate.query(
                "CALL sp_relatorio_contratos(?, ?)",
                (rs, i) -> {
                    Date inicio = rs.getDate("data_inicio");
                    Date termino = rs.getDate("data_termino");
                    return new RelatorioContrato(
                            rs.getLong("id"),
                            rs.getString("ra"),
                            rs.getString("aluno"),
                            rs.getString("curso"),
                            rs.getString("empresa"),
                            rs.getString("cargo"),
                            rs.getString("modalidade"),
                            inicio == null ? null : inicio.toLocalDate(),
                            termino == null ? null : termino.toLocalDate(),
                            rs.getString("situacao"));
                },
                situacao, empresaId);
    }
}
