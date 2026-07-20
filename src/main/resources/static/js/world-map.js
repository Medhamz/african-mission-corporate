// ============================================
// CARTE DU MONDE INTERACTIVE - VERSION AMÉLIORÉE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 world-map.js chargé');

    // Initialiser la carte
    const map = L.map('worldMap').setView([20, 0], 2);

    // Fond de carte (style épuré)
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>, &copy; CartoDB',
        subdomains: 'abcd',
        maxZoom: 19
    }).addTo(map);

    console.log('🗺️ Carte initialisée');

    // --- MARQUEURS DE TEST (pour vérifier que la carte fonctionne) ---
    // Ces marqueurs apparaissent même si l'API échoue
    L.marker([12.65, -8.0]).addTo(map)
        .bindPopup('🏛️ Bamako, Mali (siège)');
    L.marker([14.5, -14.5]).addTo(map)
        .bindPopup('🌍 Dakar, Sénégal (partenaire)');
    L.marker([6.8, -5.3]).addTo(map)
        .bindPopup('🌍 Abidjan, Côte d\'Ivoire (projet)');
    L.marker([48.9, 2.3]).addTo(map)
        .bindPopup('🌍 Paris, France (bureau)');
    L.marker([40.7, -74.0]).addTo(map)
        .bindPopup('🌍 New York, USA (investissement)');

    console.log('📍 Marqueurs de test ajoutés');

    // --- Récupération des données depuis l'API ---
    function loadCountries() {
        console.log('🔄 Chargement des données pays...');
        fetch('/api/world/countries')
            .then(res => {
                if (!res.ok) throw new Error('HTTP ' + res.status);
                return res.json();
            })
            .then(countries => {
                console.log('✅ Pays reçus :', countries);
                if (!countries || countries.length === 0) {
                    console.warn('⚠️ Aucun pays reçu');
                    return;
                }
                // On peut remplacer les marqueurs de test par les vrais, ou les superposer
                // Pour l'instant, on les ajoute en plus
                countries.forEach(country => {
                    addMarker(country);
                });
                // Adapter la vue
                const bounds = countries.map(c => [c.lat, c.lng]);
                map.fitBounds(bounds, { padding: [50, 50] });
            })
            .catch(err => {
                console.error('❌ Erreur chargement pays :', err);
                // On laisse les marqueurs de test visibles
            });
    }

    function addMarker(country) {
        // Couleur selon le type
        let color = '#3b82f6';
        let label = 'Projets';
        if (country.partners > country.projects && country.partners > 0) {
            color = '#22c55e';
            label = 'Partenariats';
        } else if (country.investment && country.investment.includes('M')) {
            color = '#f59e0b';
            label = 'Investissement';
        }

        console.log(`📍 Ajout du marqueur pour ${country.name} (${label})`);

        const marker = L.circleMarker([country.lat, country.lng], {
            radius: 8 + (country.projects || 0) * 2,
            fillColor: color,
            color: '#fff',
            weight: 2,
            opacity: 1,
            fillOpacity: 0.8,
            className: 'custom-marker'
        }).addTo(map);

        const popupContent = `
            <div class="country-popup">
                <div class="country-name">
                    <span class="country-flag">${country.flag || '🌍'}</span>
                    ${country.name}
                </div>
                <div class="country-detail"><strong>Capitale :</strong> ${country.capital || '—'}</div>
                <div class="country-detail"><strong>Projets :</strong> ${country.projects || 0}</div>
                <div class="country-detail"><strong>Partenaires :</strong> ${country.partners || 0}</div>
                <div class="country-detail"><strong>Investissement :</strong> ${country.investment || '—'}</div>
            </div>
        `;

        marker.bindPopup(popupContent, { maxWidth: 260 });
    }

    // Charger les données
    loadCountries();
});