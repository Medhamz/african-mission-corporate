package com.africanmission.controller.admin;

import com.africanmission.model.DiagnosticAnswer;
import com.africanmission.model.DiagnosticQuestion;
import com.africanmission.service.DiagnosticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/diagnostic")
@RequiredArgsConstructor
public class AdminDiagnosticController {

    private final DiagnosticService diagnosticService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("questions", diagnosticService.getAllQuestions());
        model.addAttribute("pageTitle", "Gestion du Diagnostiqueur");
        return "admin/diagnostic/questions";
    }

    @GetMapping("/questions/add")
    public String addQuestionForm(Model model) {
        model.addAttribute("question", new DiagnosticQuestion());
        model.addAttribute("pageTitle", "Ajouter une question");
        return "admin/diagnostic/question-form";
    }

    @PostMapping("/questions/save")
    public String saveQuestion(@ModelAttribute DiagnosticQuestion question, RedirectAttributes redirectAttributes) {
        diagnosticService.saveQuestion(question);
        redirectAttributes.addFlashAttribute("toastMessage", "Question enregistrée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/diagnostic";
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diagnosticService.deleteQuestion(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Question supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/diagnostic";
    }

    @GetMapping("/questions/{id}/answers")
    public String manageAnswers(@PathVariable Long id, Model model) {
        DiagnosticQuestion question = diagnosticService.getQuestionById(id);
        model.addAttribute("question", question);
        model.addAttribute("answers", diagnosticService.getAnswersByQuestionId(id));
        model.addAttribute("pageTitle", "Gestion des réponses");
        return "admin/diagnostic/answers";
    }

    @PostMapping("/answers/save")
    public String saveAnswer(@ModelAttribute DiagnosticAnswer answer,
                             @RequestParam Long questionId,
                             RedirectAttributes redirectAttributes) {
        DiagnosticQuestion question = diagnosticService.getQuestionById(questionId);
        answer.setQuestion(question);
        diagnosticService.saveAnswer(answer);
        redirectAttributes.addFlashAttribute("toastMessage", "Réponse enregistrée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/diagnostic/questions/" + questionId + "/answers";
    }

    @GetMapping("/answers/delete/{id}")
    public String deleteAnswer(@PathVariable Long id,
                               @RequestParam Long questionId,
                               RedirectAttributes redirectAttributes) {
        diagnosticService.deleteAnswer(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Réponse supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/diagnostic/questions/" + questionId + "/answers";
    }
}