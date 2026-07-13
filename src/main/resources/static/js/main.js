// ============================================
// 1. AOS ANIMATION INITIALIZATION
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    AOS.init({
        duration: 800,
        once: true,
        offset: 120,
        easing: 'ease-in-out',
        disable: 'mobile' // Désactiver sur mobile pour meilleure performance
    });
});

// ============================================
// 2. NAVBAR SCROLL EFFECT (AMÉLIORÉ)
// ============================================
let lastScroll = 0;
let isNavbarHidden = false;

window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    const currentScroll = window.pageYOffset || document.documentElement.scrollTop;

    // Effet d'ombre et de fond
    if (currentScroll > 100) {
        navbar.style.boxShadow = '0 4px 30px rgba(0,0,0,0.4)';
        navbar.style.background = 'rgba(10, 22, 40, 0.95)';
        navbar.style.backdropFilter = 'blur(20px)';
        navbar.style.borderBottom = '1px solid rgba(255,255,255,0.05)';
    } else {
        navbar.style.boxShadow = 'none';
        navbar.style.background = 'linear-gradient(135deg, #0a1628 0%, #1a2a4a 100%)';
        navbar.style.backdropFilter = 'blur(10px)';
        navbar.style.borderBottom = 'none';
    }

    // Cacher/afficher la navbar en défilant (UX améliorée)
    if (currentScroll > 150 && currentScroll > lastScroll) {
        // Défilement vers le bas - cacher la navbar
        navbar.style.transform = 'translateY(-100%)';
        navbar.style.transition = 'transform 0.3s ease';
        isNavbarHidden = true;
    } else if (currentScroll < lastScroll || currentScroll < 150) {
        // Défilement vers le haut ou en haut de page - afficher la navbar
        navbar.style.transform = 'translateY(0)';
        isNavbarHidden = false;
    }

    lastScroll = currentScroll;
});

// ============================================
// 3. SMOOTH SCROLL FOR ANCHOR LINKS
// ============================================
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        const targetId = this.getAttribute('href');
        if (targetId === '#') return;

        const target = document.querySelector(targetId);
        if (target) {
            e.preventDefault();
            const navbarHeight = document.querySelector('.navbar')?.offsetHeight || 76;
            const targetPosition = target.getBoundingClientRect().top + window.pageYOffset - navbarHeight;

            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// ============================================
// 4. AUTO-DISMISS ALERTS (AMÉLIORÉ)
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const dismissBtn = alert.querySelector('.btn-close');
            if (dismissBtn) {
                dismissBtn.click();
            } else {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => { alert.remove(); }, 500);
            }
        }, 5000);
    });
});

// ============================================
// 5. BACK TO TOP BUTTON (GÉRÉ PAR LE FOOTER)
// ============================================
// Note : Le bouton retour en haut est géré dans le footer.html
// Mais on ajoute une sécurité pour le cacher si la page est courte
document.addEventListener('DOMContentLoaded', function() {
    const scrollBtn = document.getElementById('scrollToTopBtn');
    if (scrollBtn && document.body.scrollHeight < window.innerHeight * 2) {
        scrollBtn.style.display = 'none';
    }
});

// ============================================
// 6. ACTIVE LINK HIGHLIGHT (DYNAMIQUE)
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath) {
            link.classList.add('active');
        } else if (href !== '/' && currentPath.startsWith(href)) {
            link.classList.add('active');
        }
    });
});

// ============================================
// 7. COUNTER ANIMATION (COMPTEURS)
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    const counters = document.querySelectorAll('[data-count]');

    const animateCounter = (element) => {
        const target = parseInt(element.getAttribute('data-count'));
        const duration = 2000;
        const start = 0;
        const increment = target / (duration / 16);
        let current = start;

        const updateCounter = () => {
            current += increment;
            if (current < target) {
                element.textContent = Math.floor(current);
                requestAnimationFrame(updateCounter);
            } else {
                element.textContent = target;
            }
        };
        updateCounter();
    };

    // Observer pour déclencher l'animation quand l'élément est visible
    if ('IntersectionObserver' in window) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    animateCounter(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.5 });

        counters.forEach(counter => observer.observe(counter));
    } else {
        // Fallback pour les vieux navigateurs
        counters.forEach(counter => animateCounter(counter));
    }
});

// ============================================
// 8. SEARCH FUNCTIONALITY (RECHERCHE)
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.querySelector('form[action="/search"]');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const input = this.querySelector('input[name="q"]');
            if (input && input.value.trim().length < 2) {
                e.preventDefault();
                input.style.borderColor = '#ffc107';
                input.style.boxShadow = '0 0 0 3px rgba(255,193,7,0.3)';
                setTimeout(() => {
                    input.style.borderColor = '';
                    input.style.boxShadow = '';
                }, 2000);
            }
        });
    }
});

// ============================================
// 9. DARK MODE TOGGLE (PRÊT POUR FUTURE INTÉGRATION)
// ============================================
// Fonction pour basculer en mode sombre (désactivée par défaut)
// Décommentez pour activer
/*
function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
    localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
}

// Vérifier la préférence enregistrée
if (localStorage.getItem('darkMode') === 'true') {
    document.body.classList.add('dark-mode');
}
*/

// ============================================
// 10. CONSOLE LOG (MARQUE DE FABRIQUE)
// ============================================
console.log('%c🚀 African Mission Corporate', 'font-size:20px; font-weight:bold; color:#ffc107;');
console.log('%c📌 Technologies : Spring Boot 3 + Thymeleaf + Bootstrap 5 + AOS', 'font-size:12px; color:#aaa;');
console.log('%c💡 Fonctionnalités premium activées !', 'font-size:12px; color:#6c757d;');

// ============================================
// 11. GESTION DES ERREURS JAVASCRIPT
// ============================================
window.addEventListener('error', function(e) {
    console.warn('⚠️ Erreur JavaScript capturée :', e.message);
    // Envoyer l'erreur à un service de logging si nécessaire
});

// ============================================
// 12. PERFORMANCE - LAZY LOADING (OPTIONNEL)
// ============================================
// Pour les images : les balises img avec loading="lazy" sont déjà supportées
// On ajoute un support pour les images en arrière-plan CSS
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('[data-bg]').forEach(el => {
        el.style.backgroundImage = `url(${el.getAttribute('data-bg')})`;
    });
});