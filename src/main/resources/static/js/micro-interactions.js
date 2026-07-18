// ============================================
// MICRO-INTERACTIONS (animations supplémentaires)
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Effet de vague sur les boutons (optionnel)
    const buttons = document.querySelectorAll('.btn-primary-premium, .btn-secondary-premium, .btn-outline-premium, .cta-premium');
    buttons.forEach(btn => {
        btn.addEventListener('mousedown', function(e) {
            this.style.transform = 'scale(0.96)';
        });
        btn.addEventListener('mouseup', function(e) {
            this.style.transform = '';
        });
        btn.addEventListener('mouseleave', function(e) {
            this.style.transform = '';
        });
    });

    // Animation des cartes au survol (déjà gérée par CSS)
    // On peut ajouter un effet de brillance
    const cards = document.querySelectorAll('.card-premium');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transition = 'transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.4s ease';
        });
    });

    // Compteur animé pour les statistiques (si présentes)
    const stats = document.querySelectorAll('.hero-stat-number');
    if (stats.length) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const el = entry.target;
                    const text = el.textContent;
                    const number = parseInt(text.replace(/[^0-9]/g, ''));
                    if (!isNaN(number) && number > 0) {
                        let current = 0;
                        const increment = Math.ceil(number / 30);
                        const interval = setInterval(() => {
                            current += increment;
                            if (current >= number) {
                                el.textContent = text.replace(number, number);
                                clearInterval(interval);
                            } else {
                                el.textContent = text.replace(number, current);
                            }
                        }, 30);
                    }
                    observer.unobserve(el);
                }
            });
        }, { threshold: 0.5 });
        stats.forEach(stat => observer.observe(stat));
    }

    // Effet de parallaxe léger sur l'héro (déjà présent dans le CSS)
    // Option: ajouter un effet de suivi de souris sur le globe
    const globeWrapper = document.querySelector('.hero-globe-wrapper');
    if (globeWrapper) {
        document.querySelector('.hero-premium').addEventListener('mousemove', function(e) {
            const rect = this.getBoundingClientRect();
            const x = (e.clientX - rect.left) / rect.width - 0.5;
            const y = (e.clientY - rect.top) / rect.height - 0.5;
            globeWrapper.style.transform = `rotateY(${x * 15}deg) rotateX(${-y * 15}deg)`;
        });
    }
});