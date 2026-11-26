package br.com.contratacaoEstagio.dashboard;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProcedureInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public ProcedureInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS sp_resumo_dashboard");
        jdbcTemplate.execute("""
                CREATE PROCEDURE sp_resumo_dashboard(IN p_dias INT)
                BEGIN
                    SELECT
                        (SELECT COUNT(*) FROM alunos) AS total_alunos,
                        (SELECT COUNT(*) FROM alunos WHERE ativo = 1) AS alunos_ativos,
                        (SELECT COUNT(*) FROM empresa) AS total_empresas,
                        (SELECT COUNT(*) FROM empresa WHERE ativo = 1) AS empresas_ativas,
                        (SELECT COUNT(*) FROM contrato_estagio WHERE ativo = 1) AS contratos_ativos,
                        (SELECT COUNT(*) FROM contrato_estagio
                          WHERE ativo = 1
                            AND data_termino BETWEEN CURDATE()
                                AND DATE_ADD(CURDATE(), INTERVAL p_dias DAY)) AS contratos_vencendo;
                END
                """);

        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS sp_encerrar_contratos_vencidos");
        jdbcTemplate.execute("""
                CREATE PROCEDURE sp_encerrar_contratos_vencidos()
                BEGIN
                    DECLARE v_total INT DEFAULT 0;
                    UPDATE contrato_estagio
                       SET ativo = 0
                     WHERE ativo = 1
                       AND data_termino < CURDATE();
                    SET v_total = ROW_COUNT();
                    SELECT v_total AS encerrados;
                END
                """);
    }
}
