package br.com.contratacaoEstagio.relatorio;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RelatorioProcedureInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public RelatorioProcedureInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS sp_relatorio_contratos");
        jdbcTemplate.execute("""
                CREATE PROCEDURE sp_relatorio_contratos(
                    IN p_situacao VARCHAR(20),
                    IN p_empresa_id BIGINT)
                BEGIN
                    SELECT * FROM (
                        SELECT
                            c.id AS id,
                            a.ra AS ra,
                            a.nome AS aluno,
                            a.curso AS curso,
                            e.razao_social AS empresa,
                            c.cargo AS cargo,
                            c.modalidade AS modalidade,
                            c.data_inicio AS data_inicio,
                            c.data_termino AS data_termino,
                            CASE
                                WHEN c.ativo = 0 THEN 'ENCERRADO'
                                WHEN c.data_termino < CURDATE() THEN 'VENCIDO'
                                WHEN c.data_termino <= DATE_ADD(CURDATE(), INTERVAL 30 DAY) THEN 'VENCENDO'
                                ELSE 'VIGENTE'
                            END AS situacao
                        FROM contrato_estagio c
                        JOIN alunos a ON a.ra = c.aluno_ra
                        JOIN empresa e ON e.id = c.empresa_id
                        WHERE p_empresa_id = 0 OR c.empresa_id = p_empresa_id
                    ) r
                    WHERE p_situacao = '' OR r.situacao = p_situacao
                    ORDER BY r.data_termino;
                END
                """);
    }
}
