package com.africanmission.controller;

import com.africanmission.model.Activity;
import com.africanmission.model.Partner;
import com.africanmission.model.Project;
import com.africanmission.model.Testimonial;
import com.africanmission.service.ActivityService;
import com.africanmission.service.FaqService;
import com.africanmission.service.MediaService;
import com.africanmission.service.NewsletterService;
import com.africanmission.service.PartnerService;
import com.africanmission.service.ProjectService;
import com.africanmission.service.TestimonialService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ActivityService activityService;
    private final PartnerService partnerService;
    private final MediaService mediaService;
    private final ProjectService projectService;
    private final FaqService faqService;
    private final TestimonialService testimonialService;
    private final NewsletterService newsletterService;

    @GetMapping("/")
    public String home(Model model) {
        List<Activity> activities = activityService.getAllActiveActivities();
        List<Partner> partners = partnerService.getAllActivePartners();

        model.addAttribute("activities", activities);
        model.addAttribute("partners", partners);
        model.addAttribute("pageTitle", "African Mission Corporate - Excellence et Innovation");

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        List<Partner> partners = partnerService.getAllActivePartners();
        model.addAttribute("partners", partners);
        model.addAttribute("pageTitle", "À propos - African Mission Corporate");
        return "about";
    }

    @GetMapping("/partners")
    public String partners(Model model) {
        List<Partner> partners = partnerService.getAllActivePartners();
        model.addAttribute("partners", partners);
        model.addAttribute("pageTitle", "Nos Partenaires - African Mission Corporate");
        return "partners";
    }

    @GetMapping("/activities")
    public String activities(Model model) {
        List<Activity> activities = activityService.getAllActiveActivities();
        List<String> categories = activityService.getAllCategories();

        model.addAttribute("activities", activities);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Nos activités - African Mission Corporate");

        return "activities";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("pageTitle", "Nos services - African Mission Corporate");
        return "services";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        List<Project> projects = projectService.getActiveProjects();
        List<String> categories = projectService.getAllCategories();

        model.addAttribute("projects", projects);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Nos projets - African Mission Corporate");
        return "projects";
    }

    @GetMapping("/team")
    public String team(Model model) {
        model.addAttribute("pageTitle", "Notre équipe - African Mission Corporate");
        return "team";
    }

    @GetMapping("/careers")
    public String careers(Model model) {
        model.addAttribute("pageTitle", "Carrières - African Mission Corporate");
        return "careers";
    }

    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("pageTitle", "Blog - African Mission Corporate");
        return "blog";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("faqs", faqService.getActiveFaqs());
        model.addAttribute("pageTitle", "FAQ - African Mission Corporate");
        return "faq";
    }

    @GetMapping("/legal")
    public String legal(Model model) {
        model.addAttribute("pageTitle", "Mentions légales - African Mission Corporate");
        return "legal";
    }

    @GetMapping("/sitemap")
    public String sitemap(Model model) {
        model.addAttribute("pageTitle", "Plan du site - African Mission Corporate");
        return "sitemap";
    }

    @GetMapping("/testimonials")
    public String testimonials(Model model) {
        model.addAttribute("testimonials", testimonialService.getApprovedTestimonials());
        model.addAttribute("pageTitle", "Témoignages - African Mission Corporate");
        return "testimonials";
    }

    @PostMapping("/testimonials/submit")
    public String submitTestimonial(@RequestParam String clientName,
                                    @RequestParam String content,
                                    @RequestParam(required = false) String company,
                                    @RequestParam(defaultValue = "5") Integer rating,
                                    RedirectAttributes redirectAttributes) {
        Testimonial testimonial = new Testimonial();
        testimonial.setClientName(clientName);
        testimonial.setContent(content);
        testimonial.setCompany(company);
        testimonial.setRating(rating);
        testimonial.setIsApproved(false);
        testimonialService.save(testimonial);

        redirectAttributes.addFlashAttribute("successMessage", "Merci pour votre témoignage ! Il sera publié dès validation par notre équipe.");
        return "redirect:/testimonials";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("mediaList", mediaService.getAllActiveImages());
        model.addAttribute("pageTitle", "Galerie - African Mission Corporate");
        return "gallery";
    }

    @GetMapping("/key-figures")
    public String keyFigures(Model model) {
        model.addAttribute("pageTitle", "Chiffres Clés - African Mission Corporate");
        return "key-figures";
    }

    @GetMapping("/devis")
    public String devisRedirect() {
        return "redirect:/contact?type=devis";
    }

    // GESTION DE LA NEWSLETTER (FRONT-OFFICE)
    @PostMapping({"/newsletter/subscribe", "/api/newsletter/subscribe"})
    public Object subscribeNewsletter(@RequestParam("email") String email,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        try {
            newsletterService.subscribe(email);
            if (isAjax) {
                return ResponseEntity.ok("Inscription réussie à la newsletter !");
            }
            redirectAttributes.addFlashAttribute("newsletterSuccess", "Merci pour votre inscription à notre newsletter !");
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (isAjax) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("newsletterError", e.getMessage());
        } catch (Exception e) {
            if (isAjax) {
                return ResponseEntity.internalServerError().body("Une erreur est survenue lors de l'inscription.");
            }
            redirectAttributes.addFlashAttribute("newsletterError", "Une erreur s'est produite. Veuillez réespayer.");
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}