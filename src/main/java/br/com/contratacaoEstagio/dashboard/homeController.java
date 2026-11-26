package br.com.contratacaoEstagio.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class homeController {
    private static final int DIAS_ALERTA = 30;
    private final DashboardRepository dashboardRepository;

    public homeController(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        DashboardResumo resumo = dashboardRepository.buscarResumo(DIAS_ALERTA);

        model.addAttribute("totalAlunos", resumo.totalAlunos());
        model.addAttribute("alunosAtivos", resumo.alunosAtivos());
        model.addAttribute("totalEmpresa", resumo.totalEmpresas());
        model.addAttribute("empresasAtivas", resumo.empresasAtivas());
        model.addAttribute("contratosAtivos", resumo.contratosAtivos());
        model.addAttribute("contratosVencendo", resumo.contratosVencendo());
        return "dashboard";
    }

    @PostMapping("/dashboard/encerrar-vencidos")
    public String encerrarVencidos(RedirectAttributes redirect) {
        int total = dashboardRepository.encerrarContratosVencidos();
        String mensagem = total == 0
                ? "Nenhum contrato vencido para encerrar."
                : total + " contrato(s) vencido(s) encerrado(s).";
        redirect.addFlashAttribute("mensagem", mensagem);
        return "redirect:/";
    }
}
