// ============================================
// AOS Animation Initialization
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    AOS.init({
        duration: 800,
        once: true,
        offset: 120,
        easing: 'ease-in-out'
    });
});

// ============================================
// Navbar Scroll Effect
// ============================================
let lastScroll = 0;
window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    const currentScroll = window.pageYOffset || document.documentElement.scrollTop;

    if (currentScroll > 100) {
        navbar.style.boxShadow = '0 4px 30px rgba(0,0,0,0.3)';
        navbar.style.background = 'rgba(10, 22, 40, 0.95)';
        navbar.style.backdropFilter = 'blur(20px)';
    } else {
        navbar.style.boxShadow = 'none';
        navbar.style.background = 'linear-gradient(135deg, #0a1628 0%, #1a2a4a 100%)';
        navbar.style.backdropFilter = 'blur(10px)';
    }
    lastScroll = currentScroll;
});

// ============================================
// Smooth Scroll for Anchor Links
// ============================================
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});

// ============================================
// Activity Search / Filter (si nécessaire)
// ============================================
// Exemple de filtre simple si vous avez des catégories
// À implémenter si vous voulez filtrer les activités côté client

// ============================================
// Auto-dismiss Alerts
// ============================================
const alerts = document.querySelectorAll('.alert');
alerts.forEach(alert => {
    setTimeout(() => {
        const dismissBtn = alert.querySelector('.btn-close');
        if (dismissBtn) {
            dismissBtn.click();
        }
    }, 5000);
});

console.log('🚀 African Mission Corporate - Site vitrine chargé avec succès !');
console.log('📌 Technologies : Spring Boot 3 + Thymeleaf + Bootstrap 5 + AOS');