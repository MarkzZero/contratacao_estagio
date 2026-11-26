package br.com.contratacaoEstagio.relatorio;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.contratacaoEstagio.empresa.EmpresaService;

@Controller
@RequestMapping("/relatorios/contratos")
public class RelatorioController {

    private static final Set<String> SITUACOES =
            Set.of("VIGENTE", "VENCENDO", "VENCIDO", "ENCERRADO");

    private final RelatorioRepository relatorioRepository;
    private final EmpresaService empresaService;

    public RelatorioController(RelatorioRepository relatorioRepository,
            EmpresaService empresaService) {
        this.relatorioRepository = relatorioRepository;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String pagina(
            @RequestParam(defaultValue = "") String situacao,
            @RequestParam(defaultValue = "0") long empresaId,
            Model model) {

        String filtro = normalizar(situacao);
        List<RelatorioContrato> linhas = relatorioRepository.buscarContratos(filtro, empresaId);

        model.addAttribute("linhas", linhas);
        model.addAttribute("empresas", empresaService.listarTodos());
        model.addAttribute("situacaoSelecionada", filtro);
        model.addAttribute("empresaSelecionada", empresaId);
        return "relatorio/contratos";
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(defaultValue = "") String situacao,
            @RequestParam(defaultValue = "0") long empresaId) {

        List<RelatorioContrato> linhas =
                relatorioRepository.buscarContratos(normalizar(situacao), empresaId);

        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("ID;RA;Aluno;Curso;Empresa;Cargo;Modalidade;Inicio;Termino;Situacao\n");
        for (RelatorioContrato l : linhas) {
            csv.append(campo(l.id())).append(';')
                    .append(campo(l.ra())).append(';')
                    .append(campo(l.aluno())).append(';')
                    .append(campo(l.curso())).append(';')
                    .append(campo(l.empresa())).append(';')
                    .append(campo(l.cargo())).append(';')
                    .append(campo(l.modalidade())).append(';')
                    .append(campo(l.dataInicio())).append(';')
                    .append(campo(l.dataTermino())).append(';')
                    .append(campo(l.situacao())).append('\n');
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=relatorio_contratos.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String normalizar(String situacao) {
        String s = situacao == null ? "" : situacao.trim().toUpperCase();
        return SITUACOES.contains(s) ? s : "";
    }

    private String campo(Object valor) {
        String texto = valor == null ? "" : valor.toString();
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}
