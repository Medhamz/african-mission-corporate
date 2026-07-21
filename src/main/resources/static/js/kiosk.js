// ============================================
// MODE KIOSQUE - LOGIQUE PRINCIPALE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Mode Kiosque chargé');

    const slides = document.querySelectorAll('.kiosk-slide');
    const dots = document.querySelectorAll('.kiosk-dot');
    const counter = document.getElementById('kioskCounter');
    const prevBtn = document.getElementById('kioskPrev');
    const nextBtn = document.getElementById('kioskNext');
    const exitBtn = document.getElementById('kioskExit');
    const fullscreenBtn = document.getElementById('kioskFullscreen');
    const autoPlayBtn = document.getElementById('kioskAutoPlay');

    let currentSlide = 1;
    const totalSlides = slides.length;
    let isAutoPlay = true;
    let autoPlayInterval = null;
    const AUTO_PLAY_DELAY = 5000;

    // === NAVIGATION ===
    function goToSlide(index) {
        if (index < 1) index = totalSlides;
        if (index > totalSlides) index = 1;

        // Mettre à jour les slides
        slides.forEach((slide, i) => {
            slide.classList.toggle('active', i === index - 1);
        });

        // Mettre à jour les dots
        dots.forEach((dot, i) => {
            dot.classList.toggle('active', i === index - 1);
        });

        // Mettre à jour le compteur
        counter.textContent = `${index} / ${totalSlides}`;

        currentSlide = index;

        // Déclencher les animations GSAP
        triggerSlideAnimations(index);
    }

    function nextSlide() {
        goToSlide(currentSlide + 1);
    }

    function prevSlide() {
        goToSlide(currentSlide - 1);
    }

    // === ANIMATIONS GSAP ===
    function triggerSlideAnimations(index) {
        const slide = document.querySelector(`.kiosk-slide[data-slide="${index}"]`);
        if (!slide) return;

        const content = slide.querySelector('.kiosk-slide-content');
        if (!content) return;

        // Réinitialiser les animations
        gsap.set(content.querySelectorAll('.kiosk-card, .kiosk-stat, .kiosk-cta, .kiosk-btn-primary, .kiosk-social'), {
            opacity: 0,
            y: 30,
            scale: 0.95
        });

        // Animer les éléments au sein du slide actif
        const elements = content.querySelectorAll('.kiosk-card, .kiosk-stat, .kiosk-cta, .kiosk-btn-primary, .kiosk-social');
        gsap.to(elements, {
            opacity: 1,
            y: 0,
            scale: 1,
            duration: 0.8,
            stagger: 0.15,
            ease: 'power3.out',
            delay: 0.2
        });

        // Animation spéciale pour les statistiques (compteur)
        const statNumbers = slide.querySelectorAll('.kiosk-stat-number');
        statNumbers.forEach(stat => {
            const target = parseInt(stat.dataset.count) || 0;
            gsap.fromTo(stat,
                { textContent: 0 },
                {
                    textContent: target,
                    duration: 2,
                    ease: 'power2.out',
                    snap: { textContent: 1 },
                    delay: 0.4,
                    onUpdate: function() {
                        if (target > 100) {
                            stat.textContent = Math.round(this.targets()[0].textContent);
                        }
                    }
                }
            );
        });
    }

    // === AUTO-PLAY ===
    function startAutoPlay() {
        if (autoPlayInterval) clearInterval(autoPlayInterval);
        autoPlayInterval = setInterval(nextSlide, AUTO_PLAY_DELAY);
        autoPlayBtn.classList.add('active');
        isAutoPlay = true;
    }

    function stopAutoPlay() {
        if (autoPlayInterval) {
            clearInterval(autoPlayInterval);
            autoPlayInterval = null;
        }
        autoPlayBtn.classList.remove('active');
        isAutoPlay = false;
    }

    function toggleAutoPlay() {
        if (isAutoPlay) {
            stopAutoPlay();
        } else {
            startAutoPlay();
        }
    }

    // === GESTION DU PLEIN ÉCRAN ===
    function toggleFullscreen() {
        if (!document.fullscreenElement) {
            document.documentElement.requestFullscreen().catch(err => {
                console.warn('Fullscreen non disponible:', err);
            });
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
        }
    }

    // === QUITTER LE MODE KIOSQUE ===
    function exitKiosk() {
        if (document.fullscreenElement) {
            document.exitFullscreen();
        }
        window.location.href = '/';
    }

    // === ÉVÉNEMENTS ===
    // Navigation
    nextBtn.addEventListener('click', () => {
        stopAutoPlay();
        nextSlide();
    });

    prevBtn.addEventListener('click', () => {
        stopAutoPlay();
        prevSlide();
    });

    // Dots
    dots.forEach((dot, index) => {
        dot.addEventListener('click', () => {
            stopAutoPlay();
            goToSlide(index + 1);
        });
    });

    // Touches clavier
    document.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowRight' || e.key === ' ') {
            e.preventDefault();
            stopAutoPlay();
            nextSlide();
        } else if (e.key === 'ArrowLeft') {
            e.preventDefault();
            stopAutoPlay();
            prevSlide();
        } else if (e.key === 'Escape') {
            exitKiosk();
        } else if (e.key === 'f' || e.key === 'F') {
            toggleFullscreen();
        }
    });

    // Swipe tactile
    let touchStartX = 0;
    document.addEventListener('touchstart', (e) => {
        touchStartX = e.changedTouches[0].screenX;
    });

    document.addEventListener('touchend', (e) => {
        const diff = touchStartX - e.changedTouches[0].screenX;
        if (Math.abs(diff) > 50) {
            stopAutoPlay();
            if (diff > 0) {
                nextSlide();
            } else {
                prevSlide();
            }
        }
    });

    // Plein écran
    fullscreenBtn.addEventListener('click', toggleFullscreen);

    // Auto-play
    autoPlayBtn.addEventListener('click', toggleAutoPlay);

    // Quitter
    exitBtn.addEventListener('click', exitKiosk);

    // === INITIALISATION ===
    goToSlide(1);
    startAutoPlay();

    // Forcer le refresh de la taille de la carte si présente
    setTimeout(() => {
        window.dispatchEvent(new Event('resize'));
    }, 100);

    console.log('✅ Mode Kiosque initialisé');
});