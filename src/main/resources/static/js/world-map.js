// ============================================
// CARTE DU MONDE INTERACTIVE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 world-map.js chargé');

    // Récupérer l'ID de la carte (correspond à <div id="worldMap"> dans le HTML)
    const mapContainer = document.getElementById('worldMap');
    if (!mapContainer) {
        console.error('❌ #worldMap introuvable dans le DOM');
        return;
    }

    // Initialiser la carte
    const map = L.map('worldMap').setView([20, 0], 2);

    // Fond de carte (style épuré)
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>, &copy; CartoDB',
        subdomains: 'abcd',
        maxZoom: 19
    }).addTo(map);

    console.log('🗺️ Carte initialisée');

    // --- MARQUEURS DE TEST ---
    // Ces marqueurs apparaissent immédiatement pour vérifier que la carte fonctionne
    const testMarkers = [
        { lat: 12.65, lng: -8.0, label: '🏛️ Bamako, Mali (siège)' },
        { lat: 14.5, lng: -14.5, label: '🌍 Dakar, Sénégal (partenaire)' },
        { lat: 6.8, lng: -5.3, label: '🌍 Abidjan, Côte d\'Ivoire (projet)' },
        { lat: 48.9, lng: 2.3, label: '🌍 Paris, France (bureau)' },
        { lat: 40.7, lng: -74.0, label: '🌍 New York, USA (investissement)' }
    ];

    testMarkers.forEach(m => {
        L.marker([m.lat, m.lng]).addTo(map)
            .bindPopup(m.label);
    });
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
                countries.forEach(country => {
                    addMarker(country);
                });
                // Adapter la vue
                const bounds = countries.map(c => [c.lat, c.lng]);
                map.fitBounds(bounds, { padding: [50, 50] });
            })
            .catch(err => {
                console.error('❌ Erreur chargement pays :', err);
            });
    }

    function addMarker(country) {
        let color = '#3b82f6';
        if (country.partners > country.projects && country.partners > 0) {
            color = '#22c55e';
        } else if (country.investment && country.investment.includes('M')) {
            color = '#f59e0b';
        }

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