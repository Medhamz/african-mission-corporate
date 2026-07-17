package com.africanmission.controller;

import com.africanmission.model.*;
import com.africanmission.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.africanmission.service.MediaService;
import com.africanmission.service.PageService;
import com.africanmission.service.MaintenanceService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final AdminRoleService adminRoleService;
    private final NotificationService notificationService;
    private final MediaService mediaService;
    private final PageService pageService;
    private final MaintenanceService maintenanceService;

    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // ============================================
        // STATISTIQUES DE BASE
        // ============================================
        model.addAttribute("activitiesCount", activityService.getAllActiveActivities().size());
        model.addAttribute("partnersCount", partnerService.getAllPartners().size());
        model.addAttribute("messagesCount", contactService.getAllMessages().size());
        model.addAttribute("subscribersCount", newsletterService.getSubscriberCount());
        model.addAttribute("activeChatSessions", chatSessionService.getActiveSessions().size());
        model.addAttribute("pendingTestimonials", testimonialService.countPending());
        model.addAttribute("adminUsersCount", adminUserService.countActiveUsers());
        model.addAttribute("recentLogs", adminLogService.getRecentLogs());
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount());

        // ============================================
        // DONNÉES POUR LES GRAPHIQUES
        // ============================================

        // 1. Activités par catégorie
        List<Activity> allActivities = activityService.getAllActiveActivities();
        Map<String, Long> categoryMap = allActivities.stream()
                .collect(Collectors.groupingBy(Activity::getCategory, Collectors.counting()));
        model.addAttribute("activityCategories", new ArrayList<>(categoryMap.keySet()));
        model.addAttribute("activityCategoryCounts", new ArrayList<>(categoryMap.values()));

        // 2. Évolution des messages (7 derniers jours)
        List<ContactMessage> allMessages = contactService.getAllMessages();
        Map<LocalDate, Long> messageDateMap = allMessages.stream()
                .filter(m -> m.getCreatedAt() != null)
                .filter(m -> m.getCreatedAt().toLocalDate().isAfter(LocalDate.now().minusDays(7)))
                .collect(Collectors.groupingBy(m -> m.getCreatedAt().toLocalDate(), Collectors.counting()));

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("dd/MM")));
            counts.add(messageDateMap.getOrDefault(date, 0L));
        }
        model.addAttribute("messageDates", dates);
        model.addAttribute("messageCounts", counts);

        // 3. Répartition des messages lus/non lus
        long readCount = allMessages.stream().filter(ContactMessage::getIsRead).count();
        long unreadCount = allMessages.size() - readCount;
        model.addAttribute("readMessagesCount", readCount);
        model.addAttribute("unreadMessagesCount", unreadCount);

        // 4. Sessions de chat (7 derniers jours)
        List<ChatSession> allSessions = chatSessionService.getActiveSessions();
        Map<LocalDate, Long> chatDateMap = allSessions.stream()
                .filter(s -> s.getLastActivity() != null)
                .filter(s -> s.getLastActivity().toLocalDate().isAfter(LocalDate.now().minusDays(7)))
                .collect(Collectors.groupingBy(s -> s.getLastActivity().toLocalDate(), Collectors.counting()));

        List<String> chatDates = new ArrayList<>();
        List<Long> chatCounts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            chatDates.add(date.format(DateTimeFormatter.ofPattern("dd/MM")));
            chatCounts.add(chatDateMap.getOrDefault(date, 0L));
        }
        model.addAttribute("chatDates", chatDates);
        model.addAttribute("chatCounts", chatCounts);

        // 5. Tendances (croissance) - À adapter selon vos données réelles
        model.addAttribute("activitiesGrowth", 5);
        model.addAttribute("partnersGrowth", 2);
        model.addAttribute("messagesGrowth", 8);
        model.addAttribute("subscribersGrowth", 3);

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
    // ACTIVITÉS
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
    // PARTENAIRES
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
    // MESSAGES DE CONTACT
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
    // CHAT
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
    // NEWSLETTER
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
        try {
            model.addAttribute("faqs", faqService.getAllFaqs());
            model.addAttribute("categories", faqService.getAllCategories());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur : " + e.getMessage());
            model.addAttribute("faqs", List.of());
            model.addAttribute("categories", List.of());
        }
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

    // ============================================
    // GESTION DES RÔLES
    // ============================================
    @GetMapping("/roles")
    public String manageRoles(Model model) {
        model.addAttribute("roles", adminRoleService.getAllRoles());
        model.addAttribute("pageTitle", "Gestion des rôles");
        return "admin/roles";
    }

    @PostMapping("/roles/add")
    public String addRole(@RequestParam String name,
                          @RequestParam String description,
                          @RequestParam(required = false) String permissions,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        AdminRole role = new AdminRole();
        role.setName(name.toUpperCase());
        role.setDescription(description);
        role.setPermissions(permissions);
        adminRoleService.save(role);
        redirectAttributes.addFlashAttribute("toastMessage", "Rôle ajouté !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "ADD_ROLE", "Ajout du rôle: " + name, request.getRemoteAddr());
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        AdminRole role = adminRoleService.getRoleById(id);
        adminRoleService.delete(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Rôle supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        adminLogService.log(getCurrentUsername(), "DELETE_ROLE", "Suppression du rôle: " + role.getName(), request.getRemoteAddr());
        return "redirect:/admin/roles";
    }

    // ============================================
    // NOTIFICATIONS
    // ============================================
    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        model.addAttribute("notifications", notificationService.getUnreadNotifications());
        model.addAttribute("unreadCount", notificationService.getUnreadCount());
        model.addAttribute("pageTitle", "Notifications");
        return "admin/notifications";
    }

    @PostMapping("/notifications/mark-read/{id}")
    public String markNotificationRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.markAsRead(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Notification marquée comme lue");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/dismiss/{id}")
    public String dismissNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.dismiss(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Notification supprimée");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/dismiss-all")
    public String dismissAllNotifications(RedirectAttributes redirectAttributes) {
        notificationService.dismissAll();
        redirectAttributes.addFlashAttribute("toastMessage", "Toutes les notifications supprimées");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/mark-all-read")
    public String markAllNotificationsRead(RedirectAttributes redirectAttributes) {
        notificationService.markAllAsRead();
        redirectAttributes.addFlashAttribute("toastMessage", "Toutes les notifications marquées comme lues");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/notifications";
    }

    // ============================================
    // EXPORT DES DONNÉES
    // ============================================
    @GetMapping("/export/messages")
    public void exportMessages(HttpServletResponse response) throws IOException {
        List<ContactMessage> messages = contactService.getAllMessages();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=messages_export.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Nom,Email,Téléphone,Sujet,Message,Lu,Date");
        for (ContactMessage msg : messages) {
            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                    msg.getId(),
                    msg.getName(),
                    msg.getEmail(),
                    msg.getPhone() != null ? msg.getPhone() : "",
                    msg.getSubject() != null ? msg.getSubject() : "",
                    msg.getMessage().replace(",", " "),
                    msg.getIsRead() ? "Oui" : "Non",
                    msg.getCreatedAt()
            );
        }
        writer.flush();
    }

    @GetMapping("/export/activities")
    public void exportActivities(HttpServletResponse response) throws IOException {
        List<Activity> activities = activityService.getAllActiveActivities();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=activities_export.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Titre,Description,Catégorie,Ordre,Actif");
        for (Activity activity : activities) {
            writer.printf("%d,%s,%s,%s,%d,%s%n",
                    activity.getId(),
                    activity.getTitle(),
                    activity.getDescription() != null ? activity.getDescription().replace(",", " ") : "",
                    activity.getCategory(),
                    activity.getDisplayOrder(),
                    activity.getIsActive() ? "Oui" : "Non"
            );
        }
        writer.flush();
    }

    @GetMapping("/export/subscribers")
    public void exportSubscribers(HttpServletResponse response) throws IOException {
        List<Newsletter> subscribers = newsletterService.getAllActiveSubscribers();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=subscribers_export.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Email,Date d'inscription");
        for (Newsletter sub : subscribers) {
            writer.printf("%d,%s,%s%n",
                    sub.getId(),
                    sub.getEmail(),
                    sub.getSubscribedAt()
            );
        }
        writer.flush();
    }

    // ============================================
// GESTION DES MÉDIAS
// ============================================
    @GetMapping("/media")
    public String manageMedia(Model model) {
        model.addAttribute("media", mediaService.getAllActive());
        model.addAttribute("pageTitle", "Bibliothèque de médias");
        return "admin/media";
    }

    @PostMapping("/media/upload")
    public String uploadMedia(@RequestParam("file") MultipartFile file,
                              @RequestParam(required = false) String altText,
                              RedirectAttributes redirectAttributes) {
        try {
            mediaService.uploadFile(file, altText);
            redirectAttributes.addFlashAttribute("toastMessage", "Fichier uploadé avec succès !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/media";
    }

    @PostMapping("/media/delete/{id}")
    public String deleteMedia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mediaService.delete(id);
            redirectAttributes.addFlashAttribute("toastMessage", "Média supprimé !");
            redirectAttributes.addFlashAttribute("toastType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("toastMessage", "Erreur: " + e.getMessage());
            redirectAttributes.addFlashAttribute("toastType", "error");
        }
        return "redirect:/admin/media";
    }

    // ============================================
// GESTION DES PAGES STATIQUES
// ============================================
    @GetMapping("/pages")
    public String managePages(Model model) {
        model.addAttribute("pages", pageService.getAllPages());
        model.addAttribute("pageTitle", "Gestion des pages");
        return "admin/pages";
    }

    @GetMapping("/pages/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Page page = pageService.getById(id);
        model.addAttribute("page", page);
        model.addAttribute("pageTitle", "Modifier la page");
        return "admin/page-edit";
    }

    @PostMapping("/pages/update/{id}")
    public String updatePage(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             RedirectAttributes redirectAttributes) {
        Page page = pageService.getById(id);
        page.setTitle(title);
        page.setContent(content);
        pageService.save(page);
        redirectAttributes.addFlashAttribute("toastMessage", "Page mise à jour !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/pages";
    }

    @PostMapping("/pages/toggle/{id}")
    public String togglePage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Page page = pageService.getById(id);
        page.setIsActive(!page.getIsActive());
        pageService.save(page);
        redirectAttributes.addFlashAttribute("toastMessage", "Statut de la page modifié");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/pages";
    }

    // ============================================
// MAINTENANCE MODE
// ============================================
    @GetMapping("/maintenance")
    public String maintenanceMode(Model model) {
        model.addAttribute("isEnabled", maintenanceService.isMaintenanceMode());
        model.addAttribute("pageTitle", "Mode maintenance");
        return "admin/maintenance";
    }

    @PostMapping("/maintenance/enable")
    public String enableMaintenance(RedirectAttributes redirectAttributes) {
        maintenanceService.enableMaintenance();
        redirectAttributes.addFlashAttribute("toastMessage", "Mode maintenance activé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/maintenance";
    }

    @PostMapping("/maintenance/disable")
    public String disableMaintenance(RedirectAttributes redirectAttributes) {
        maintenanceService.disableMaintenance();
        redirectAttributes.addFlashAttribute("toastMessage", "Mode maintenance désactivé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/maintenance";
    }

    // ============================================
// RECHERCHE GLOBALE
// ============================================
    @GetMapping("/search")
    public String globalSearch(@RequestParam String q, Model model) {
        // Recherche dans les activités, partenaires, projets, pages
        List<Activity> activities = activityService.searchByName(q);
        List<Partner> partners = partnerService.searchByName(q);
        List<Project> projects = projectService.searchByTitle(q);
        List<Page> pages = pageService.searchByTitle(q);

        model.addAttribute("query", q);
        model.addAttribute("activities", activities);
        model.addAttribute("partners", partners);
        model.addAttribute("projects", projects);
        model.addAttribute("pages", pages);
        model.addAttribute("pageTitle", "Résultats de recherche");
        return "admin/search-results";
    }
}