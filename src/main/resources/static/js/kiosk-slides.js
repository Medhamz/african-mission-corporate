// ============================================
// KIOSQUE - DONNÉES DES SLIDES (ÉVOLUTIF)
// ============================================

const kioskSlides = [
    {
        id: 1,
        type: 'hero',
        title: 'Bienvenue sur notre vitrine interactive',
        subtitle: 'Découvrez l\'excellence malienne au service du développement durable',
        icon: 'fas fa-globe-africa'
    },
    {
        id: 2,
        type: 'grid',
        title: 'Nos activités principales',
        items: [
            { icon: 'fas fa-building', title: 'BTP & Construction', desc: 'Projets d\'envergure au Mali et à l\'international' },
            { icon: 'fas fa-seedling', title: 'Agrobusiness', desc: 'Innovation durable pour l\'agriculture' },
            { icon: 'fas fa-ship', title: 'Import-Export', desc: 'Connexion entre l\'Afrique et le monde' }
        ]
    },
    {
        id: 3,
        type: 'stats',
        title: 'La force des chiffres',
        stats: [
            { label: 'Activités', value: 33 },
            { label: 'Domaines', value: 15 },
            { label: '% Engagement', value: 100 },
            { label: 'Année de référence', value: 2026 }
        ]
    }
];

// Exporter pour une utilisation future
if (typeof module !== 'undefined' && module.exports) {
    module.exports = kioskSlides;
}