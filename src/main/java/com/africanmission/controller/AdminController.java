package com.africanmission.controller;

import com.africanmission.model.Activity;
import com.africanmission.model.ContactMessage;
import com.africanmission.model.Partner;
import com.africanmission.service.*;
import lombok.RequiredArgsConstructor;
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
        model.addAttribute("pendingChatCount", chatService.getPendingCount());
        model.addAttribute("pageTitle", "Dashboard - Administration");
        return "admin/dashboard";
    }

    // Gestion des activités
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
                              RedirectAttributes redirectAttributes) {
        Activity activity = new Activity();
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setCategory(category);
        activity.setIcon(icon != null ? icon : "fas fa-check-circle");
        activity.setIsActive(true);
        activityService.saveActivity(activity);
        redirectAttributes.addFlashAttribute("toastMessage", "Activité ajoutée avec succès !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/activities";
    }

    @PostMapping("/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        activityService.deleteActivity(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Activité supprimée !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/activities";
    }

    // Gestion des partenaires
    @GetMapping("/partners")
    public String managePartners(Model model) {
        model.addAttribute("partners", partnerService.getAllPartners());
        model.addAttribute("pageTitle", "Gestion des partenaires");
        return "admin/partners";
    }

    @PostMapping("/partners/add")
    public String addPartner(@RequestParam String name,
                             @RequestParam(required = false) String website,
                             RedirectAttributes redirectAttributes) {
        Partner partner = new Partner();
        partner.setName(name);
        partner.setWebsite(website);
        partner.setIsActive(true);
        partnerService.savePartner(partner);
        redirectAttributes.addFlashAttribute("toastMessage", "Partenaire ajouté avec succès !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/partners";
    }

    @PostMapping("/partners/delete/{id}")
    public String deletePartner(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        partnerService.deletePartner(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Partenaire supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/partners";
    }

    // Gestion des messages
    @GetMapping("/messages")
    public String manageMessages(Model model) {
        model.addAttribute("messages", contactService.getAllMessages());
        model.addAttribute("pageTitle", "Gestion des messages");
        return "admin/messages";
    }

    @PostMapping("/messages/read/{id}")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.markAsRead(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message marqué comme lu");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/messages";
    }

    @PostMapping("/messages/delete/{id}")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        contactService.deleteMessage(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/messages";
    }

    // Gestion du chat
    @GetMapping("/chat")
    public String manageChat(Model model) {
        model.addAttribute("pendingMessages", chatService.getPendingMessages());
        model.addAttribute("approvedMessages", chatService.getApprovedMessages());
        model.addAttribute("pageTitle", "Modération du chat");
        return "admin/chat";
    }

    @PostMapping("/chat/approve/{id}")
    public String approveChat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        chatService.approveMessage(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message approuvé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/chat";
    }

    @PostMapping("/chat/delete/{id}")
    public String deleteChat(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        chatService.deleteMessage(id);
        redirectAttributes.addFlashAttribute("toastMessage", "Message supprimé !");
        redirectAttributes.addFlashAttribute("toastType", "success");
        return "redirect:/admin/chat";
    }

    // Gestion de la newsletter
    @GetMapping("/newsletter")
    public String manageNewsletter(Model model) {
        model.addAttribute("subscribers", newsletterService.getAllActiveSubscribers());
        model.addAttribute("pageTitle", "Newsletter");
        return "admin/newsletter";
    }
}