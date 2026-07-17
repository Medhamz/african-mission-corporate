package com.africanmission.controller;

import com.africanmission.model.*;
import com.africanmission.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ActivityService activityService;
    private final PartnerService partnerService;
    private final ContactService contactService;
    private final NewsletterService newsletterService;
    private final ChatService chatService;
    private final ChatSessionService chatSessionService;
    private final AdminUserService adminUserService;
    private final AdminLogService adminLogService;
    private final SiteSettingService siteSettingService;
    private final TestimonialService testimonialService;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final FaqService faqService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activitiesCount", activityService.getAllActiveActivities().size());
        model.addAttribute("partnersCount", partnerService.getAllPartners().size());
        model.addAttribute("messagesCount", contactService.getAllMessages().size());
        model.addAttribute("subscribersCount", newsletterService.getSubscriberCount());
        model.addAttribute("activeChatSessions", chatSessionService.getActiveSessions().size());
        model.addAttribute("pendingTestimonials", testimonialService.countPending());
        model.addAttribute("adminUsersCount", adminUserService.countActiveUsers());
        model.addAttribute("recentLogs", adminLogService.getRecentLogs());
        model.addAttribute("pageTitle", "Dashboard - Administration");
        return "admin/dashboard";
    }

    // ============================================
    // GESTION DES UTILISATEURS ADMIN
    // ============================================
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", adminUserService.getAllUsers());
        model.addAttribute("pageTitle", "Gestion des utilisateurs");
        return "admin/users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          @RequestParam String fullName,
                          @RequestParam(defaultValue = "ADMIN") String role,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            AdminUser user = new AdminUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            adminUserService.createUser(user);
            redirectAttributes.addFlashAttribute("toastMessage", "Utilisateur ajouté avec succès !");
            redirectAttributes.addFlashAttribute("toastType", "success");
            adminLogService.log(getCurrentUsername(), "ADD_USER", "Ajout de l'utilisateur: " + username, request.getRemoteAddr());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        AdminUser user = adminUserService.getUserById(id);
        adminUserService.deleteUser(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Utilisateur supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_USER", "Suppression de l'utilisateur: " + user.getUsername(), request.getRemoteAddr());
        return "redirect:/admin/users";
    }

    // ============================================
    // GESTION DES PARAMÈTRES DU SITE
    // ============================================
    @GetMapping("/settings")
    public String manageSettings(Model model) {
        model.addAttribute("settings", siteSettingService.getAllSettings());
        model.addAttribute("pageTitle", "Paramètres du site");
        return "admin/settings";
    }

    @PostMapping("/settings/save")
    public String saveSetting(@RequestParam String key,
                              @RequestParam String value,
                              @RequestParam String description,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        siteSettingService.saveSetting(key, value, description);
        redirectAttributes.addFlashAttribute("toastMessage", "Paramètre enregistré !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "SAVE_SETTING", "Modification du paramètre: " + key, request.getRemoteAddr());
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/delete/{id}")
    public String deleteSetting(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SiteSetting setting = siteSettingService.getAllSettings().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        if (setting != null) {
            siteSettingService.deleteSetting(setting.getKey());
            adminLogService.log(getCurrentUsername(), "DELETE_SETTING", "Suppression du paramètre: " + setting.getKey(), request.getRemoteAddr());
        }
        redirectAttributes.addFlashAttribute("toastMessage", "Paramètre supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/settings";
    }

    // ============================================
    // GESTION DES TÉMOIGNAGES
    // ============================================
    @GetMapping("/testimonials")
    public String manageTestimonials(Model model) {
        model.addAttribute("testimonials", testimonialService.getAllTestimonials());
        model.addAttribute("pageTitle", "Gestion des témoignages");
        return "admin/testimonials";
    }

    @PostMapping("/testimonials/add")
    public String addTestimonial(@RequestParam String clientName,
                                 @RequestParam String content,
                                 @RequestParam(required = false) String company,
                                 @RequestParam(defaultValue = "5") Integer rating,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        Testimonial testimonial = new Testimonial();
        testimonial.setClientName(clientName);
        testimonial.setContent(content);
        testimonial.setCompany(company);
        testimonial.setRating(rating);
        testimonial.setIsApproved(false);
        testimonialService.save(testimonial);
        redirectAttributes.addFlashAttribute("toastMessage", "Témoignage ajouté !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_TESTIMONIAL", "Ajout d'un témoignage de: " + clientName, request.getRemoteAddr());
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/testimonials/approve/{id}")
    public String approveTestimonial(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        testimonialService.approve(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Témoignage approuvé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "APPROVE_TESTIMONIAL", "Approbation du témoignage ID: " + id, request.getRemoteAddr());
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/testimonials/delete/{id}")
    public String deleteTestimonial(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        testimonialService.delete(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Témoignage supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_TESTIMONIAL", "Suppression du témoignage ID: " + id, request.getRemoteAddr());
        return "redirect:/admin/testimonials";
    }

    // ============================================
    // GESTION DES PROJETS
    // ============================================
    @GetMapping("/projects-admin")
    public String manageProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("pageTitle", "Gestion des projets");
        return "admin/projects";
    }

    @PostMapping("/projects/add")
    public String addProject(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String category,
                             @RequestParam(required = false) String imageUrl,
                             @RequestParam(required = false) String clientName,
                             @RequestParam(required = false) String completionDate,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setCategory(category);
        project.setImageUrl(imageUrl);
        project.setClientName(clientName);
        project.setCompletionDate(completionDate);
        project.setIsActive(true);
        projectService.save(project);
        redirectAttributes.addFlashAttribute("toastMessage", "Projet ajouté !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_PROJECT", "Ajout du projet: " + title, request.getRemoteAddr());
        return "redirect:/admin/projects-admin";
    }

    @PostMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Project project = projectService.getById(id);
        projectService.delete(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Projet supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_PROJECT", "Suppression du projet: " + project.getTitle(), request.getRemoteAddr());
        return "redirect:/admin/projects-admin";
    }

    // ============================================
    // LOGS ADMIN
    // ============================================
    @GetMapping("/logs")
    public String viewLogs(Model model) {
        model.addAttribute("logs", adminLogService.getRecentLogs());
        model.addAttribute("pageTitle", "Journal d'activité");
        return "admin/logs";
    }

    @PostMapping("/logs/clear")
    public String clearLogs(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        adminLogService.clearLogs();
        redirectAttributes.addFlashAttribute("toastMessage", "Logs supprimés !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "CLEAR_LOGS", "Suppression de tous les logs", request.getRemoteAddr());
        return "redirect:/admin/logs";
    }

    // ============================================
    // ACTIVITÉS (déjà existant)
    // ============================================
    @GetMapping("/activities")
    public String manageActivities(Model model) {
        model.addAttribute("activities", activityService.getAllActiveActivities());
        model.addAttribute("pageTitle", "Gestion des activités");
        return "admin/activities";
    }

    @PostMapping("/activities/add")
    public String addActivity(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String category,
                              @RequestParam(required = false) String icon,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        Activity activity = new Activity();
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setCategory(category);
        activity.setIcon(icon != null ? icon : "fas fa-check-circle");
        activity.setIsActive(true);
        activityService.saveActivity(activity);
        redirectAttributes.addFlashAttribute("toastMessage", "Activité ajoutée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_ACTIVITY", "Ajout de l'activité: " + title, request.getRemoteAddr());
        return "redirect:/admin/activities";
    }

    @PostMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        activityService.deleteActivity(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Activité supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_ACTIVITY", "Suppression de l'activité ID: " + id, request.getRemoteAddr());
        return "redirect:/admin/activities";
    }

    // ============================================
    // PARTENAIRES (déjà existant)
    // ============================================
    @GetMapping("/partners")
    public String managePartners(Model model) {
        model.addAttribute("partners", partnerService.getAllPartners());
        model.addAttribute("pageTitle", "Gestion des partenaires");
        return "admin/partners";
    }

    @PostMapping("/partners/add")
    public String addPartner(@RequestParam String name,
                             @RequestParam(required = false) String website,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        Partner partner = new Partner();
        partner.setName(name);
        partner.setWebsite(website);
        partner.setIsActive(true);
        partnerService.savePartner(partner);
        redirectAttributes.addFlashAttribute("toastMessage", "Partenaire ajouté !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_PARTNER", "Ajout du partenaire: " + name, request.getRemoteAddr());
        return "redirect:/admin/partners";
    }

    @PostMapping("/partners/delete/{id}")
    public String deletePartner(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        partnerService.deletePartner(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Partenaire supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_PARTNER", "Suppression du partenaire ID: " + id, request.getRemoteAddr());
        return "redirect:/admin/partners";
    }

    // ============================================
    // MESSAGES DE CONTACT (déjà existant)
    // ============================================
    @GetMapping("/messages")
    public String manageMessages(Model model) {
        model.addAttribute("messages", contactService.getAllMessages());
        model.addAttribute("pageTitle", "Gestion des messages");
        return "admin/messages";
    }

    @PostMapping("/messages/read/{id}")
    public String markAsRead(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        contactService.markAsRead(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message marqué comme lu");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "MARK_READ", "Message ID: " + id + " marqué comme lu", request.getRemoteAddr());
        return "redirect:/admin/messages";
    }

    @PostMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        contactService.deleteMessage(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_MESSAGE", "Suppression du message ID: " + id, request.getRemoteAddr());
        return "redirect:/admin/messages";
    }

    // ============================================
    // CHAT (déjà existant)
    // ============================================
    @GetMapping("/chat")
    public String manageChat() {
        return "redirect:/admin/chat-sessions";
    }

    @GetMapping("/chat-sessions")
    public String chatSessions(Model model) {
        model.addAttribute("pageTitle", "Chat en direct - Administration");
        return "admin/chat-sessions";
    }

    // ============================================
    // NEWSLETTER (déjà existant)
    // ============================================
    @GetMapping("/newsletter")
    public String manageNewsletter(Model model) {
        model.addAttribute("subscribers", newsletterService.getAllActiveSubscribers());
        model.addAttribute("pageTitle", "Newsletter");
        return "admin/newsletter";
    }

    // ============================================
    // MÉTHODE UTILITAIRE
    // ============================================
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }

    // ============================================
// GESTION DES MEMBRES DE L'ÉQUIPE
// ============================================
    @GetMapping("/team-members")
    public String manageTeamMembers(Model model) {
        model.addAttribute("members", teamMemberService.getAllMembers());
        model.addAttribute("pageTitle", "Gestion de l'équipe");
        return "admin/team-members";
    }

    @PostMapping("/team-members/add")
    public String addTeamMember(@RequestParam String name,
                                @RequestParam String position,
                                @RequestParam(required = false) String bio,
                                @RequestParam(required = false) String photoUrl,
                                @RequestParam(required = false) String linkedinUrl,
                                @RequestParam(required = false) String twitterUrl,
                                @RequestParam(defaultValue = "0") Integer displayOrder,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        TeamMember member = new TeamMember();
        member.setName(name);
        member.setPosition(position);
        member.setBio(bio);
        member.setPhotoUrl(photoUrl);
        member.setLinkedinUrl(linkedinUrl);
        member.setTwitterUrl(twitterUrl);
        member.setDisplayOrder(displayOrder);
        member.setIsActive(true);
        teamMemberService.save(member);
        redirectAttributes.addFlashAttribute("toastMessage", "Membre ajouté !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_TEAM_MEMBER", "Ajout du membre: " + name, request.getRemoteAddr());
        return "redirect:/admin/team-members";
    }

    @PostMapping("/team-members/delete/{id}")
    public String deleteTeamMember(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        TeamMember member = teamMemberService.getMemberById(id);
        teamMemberService.delete(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Membre supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_TEAM_MEMBER", "Suppression du membre: " + member.getName(), request.getRemoteAddr());
        return "redirect:/admin/team-members";
    }

    @PostMapping("/team-members/toggle/{id}")
    public String toggleTeamMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        TeamMember member = teamMemberService.getMemberById(id);
        member.setIsActive(!member.getIsActive());
        teamMemberService.save(member);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut du membre mis à jour");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/team-members";
    }

    // ============================================
// GESTION DES FAQ
// ============================================
    @GetMapping("/faqs")
    public String manageFaqs(Model model) {
        model.addAttribute("faqs", faqService.getAllFaqs());
        model.addAttribute("categories", faqService.getAllCategories());
        model.addAttribute("pageTitle", "Gestion des FAQ");
        return "admin/faqs";
    }

    @PostMapping("/faqs/add")
    public String addFaq(@RequestParam String question,
                         @RequestParam String answer,
                         @RequestParam(required = false) String category,
                         @RequestParam(defaultValue = "0") Integer displayOrder,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setCategory(category);
        faq.setDisplayOrder(displayOrder);
        faq.setIsActive(true);
        faqService.save(faq);
        redirectAttributes.addFlashAttribute("toastMessage", "FAQ ajoutée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_FAQ", "Ajout de la FAQ: " + question, request.getRemoteAddr());
        return "redirect:/admin/faqs";
    }

    @PostMapping("/faqs/delete/{id}")
    public String deleteFaq(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Faq faq = faqService.getFaqById(id);
        faqService.delete(id);
        redirectAttributes.addFlashAttribute("toastMessage", "FAQ supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_FAQ", "Suppression de la FAQ: " + faq.getQuestion(), request.getRemoteAddr());
        return "redirect:/admin/faqs";
    }
}