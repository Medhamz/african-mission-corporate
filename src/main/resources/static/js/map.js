// ============================================
// CARTE DU MONDE INTERACTIVE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Initialiser la carte
    const map = L.map('map').setView([20, 0], 2); // Vue monde

    // Ajouter le fond de carte (OpenStreetMap)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        attribution: '© OpenStreetMap'
    }).addTo(map);

    // Données des projets / clients / fournisseurs
    const locations = [
        {
            name: "African Mission - Bamako",
            lat: 12.6392,
            lng: -8.0029,
            type: "project",
            description: "Siège social et activités BTP",
            weather: "Ensoleillé",
            temp: "32°C",
            risk: "Faible"
        },
        {
            name: "Projet agricole Sikasso",
            lat: 11.3167,
            lng: -5.6667,
            type: "project",
            description: "Exploitation agricole et transformation",
            weather: "Nuageux",
            temp: "28°C",
            risk: "Modéré"
        },
        {
            name: "Port de Dakar",
            lat: 14.7167,
            lng: -17.4677,
            type: "import",
            description: "Import-Export de matières premières",
            weather: "Venté",
            temp: "26°C",
            risk: "Élevé"
        },
        {
            name: "Fournisseur Coton - Bouaké",
            lat: 7.6833,
            lng: -5.0333,
            type: "export",
            description: "Cotton supplier",
            weather: "Pluvieux",
            temp: "24°C",
            risk: "Faible"
        },
        {
            name: "Projet Rénovation - Abidjan",
            lat: 5.3600,
            lng: -4.0083,
            type: "project",
            description: "Rénovation d'infrastructures",
            weather: "Ensoleillé",
            temp: "30°C",
            risk: "Faible"
        }
    ];

    // Charger les données météo et marchés pour enrichir les infos
    async function enrichWithRealtimeData() {
        try {
            const [weatherRes, marketRes] = await Promise.all([
                fetch('/api/market/weather'),
                fetch('/api/market/prices')
            ]);
            const weather = await weatherRes.json();
            const markets = await marketRes.json();

            // Mettre à jour les données de localisation avec les valeurs réelles
            // (simulé ici pour l'exemple)
            if (weather && weather.main) {
                locations.forEach(loc => {
                    if (loc.name.includes('Bamako') || loc.name.includes('Dakar')) {
                        loc.temp = Math.round(weather.main.temp) + '°C';
                        loc.weather = weather.weather?.[0]?.description || '--';
                    }
                });
            }
            if (markets && Array.isArray(markets)) {
                const steel = markets.find(m => m.symbol === 'STEEL');
                if (steel) {
                    locations.forEach(loc => {
                        if (loc.type === 'import' || loc.type === 'export') {
                            loc.risk = parseFloat(steel.change) > 0 ? 'Élevé' : 'Modéré';
                        }
                    });
                }
            }
        } catch (e) {
            console.warn('Erreur chargement données temps réel :', e);
        }
    }

    // Fonction pour créer un marqueur personnalisé
    function createMarker(location) {
        let color = '#3b82f6';
        if (location.type === 'import') color = '#f59e0b';
        else if (location.type === 'export') color = '#22c55e';

        const icon = L.divIcon({
            className: 'custom-marker',
            html: `<div style="background:${color};width:16px;height:16px;border-radius:50%;border:2px solid #fff;box-shadow:0 0 0 4px rgba(0,0,0,0.1);"></div>`,
            iconSize: [16, 16],
            iconAnchor: [8, 8]
        });

        const popupContent = `
            <div style="max-width:280px;">
                <h6 style="font-weight:700;color:var(--text-primary);margin-bottom:6px;">${location.name}</h6>
                <p style="font-size:0.85rem;color:var(--text-secondary);margin-bottom:4px;">${location.description}</p>
                <div style="font-size:0.8rem;color:var(--text-muted);">
                    <i class="fas fa-cloud"></i> ${location.weather} ${location.temp}
                    <br>
                    <i class="fas fa-shield-alt"></i> Risque : ${location.risk}
                </div>
            </div>
        `;

        return L.marker([location.lat, location.lng], { icon })
            .bindPopup(popupContent)
            .openPopup();
    }

    // Ajouter les marqueurs
    async function loadMap() {
        await enrichWithRealtimeData();
        locations.forEach(loc => createMarker(loc));

        // Ajouter une légende
        const legend = L.control({ position: 'bottomright' });
        legend.onAdd = function() {
            const div = L.DomUtil.create('div', 'legend');
            div.innerHTML = `
                <div style="display:flex;flex-direction:column;gap:4px;">
                    <div><span style="background:#3b82f6;display:inline-block;width:12px;height:12px;border-radius:50%;margin-right:6px;"></span> Projets</div>
                    <div><span style="background:#f59e0b;display:inline-block;width:12px;height:12px;border-radius:50%;margin-right:6px;"></span> Import</div>
                    <div><span style="background:#22c55e;display:inline-block;width:12px;height:12px;border-radius:50%;margin-right:6px;"></span> Export</div>
                </div>
            `;
            return div;
        };
        legend.addTo(map);
    }

    loadMap();
});