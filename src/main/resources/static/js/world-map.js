// ============================================
// CARTE DU MONDE INTERACTIVE - VERSION PREMIUM
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 world-map.js (premium) chargé');

    const mapContainer = document.getElementById('worldMap');
    if (!mapContainer) {
        console.error('❌ #worldMap introuvable');
        return;
    }

    // === INITIALISATION DE LA CARTE ===
    const map = L.map('worldMap').setView([20, 0], 2);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>, &copy; CartoDB',
        subdomains: 'abcd',
        maxZoom: 19
    }).addTo(map);

    console.log('🗺️ Carte initialisée');

    // === VARIABLES GLOBALES ===
    let allMarkers = [];
    let currentFilter = 'all';

    // === DONNÉES DES PAYS ===
    const countryData = [
        { name: 'Mali', lat: 12.65, lng: -8.0, capital: 'Bamako', projects: 3, partners: 2, investment: '1.2M FCFA', flag: '🇲🇱', type: 'project' },
        { name: 'Sénégal', lat: 14.5, lng: -14.5, capital: 'Dakar', projects: 2, partners: 1, investment: '850K FCFA', flag: '🇸🇳', type: 'partner' },
        { name: "Côte d'Ivoire", lat: 6.8, lng: -5.3, capital: 'Abidjan', projects: 1, partners: 0, investment: '620K FCFA', flag: '🇨🇮', type: 'project' },
        { name: 'France', lat: 48.9, lng: 2.3, capital: 'Paris', projects: 1, partners: 1, investment: '2.1M FCFA', flag: '🇫🇷', type: 'investment' },
        { name: 'États-Unis', lat: 40.7, lng: -74.0, capital: 'New York', projects: 0, partners: 1, investment: '3.5M FCFA', flag: '🇺🇸', type: 'investment' }
    ];

    // === MARQUEURS AVEC ICÔNES PERSONNALISÉES ===
    function createIcon(type) {
        let color, iconClass;
        if (type === 'project') {
            color = '#3b82f6';
            iconClass = 'fa-industry';
        } else if (type === 'partner') {
            color = '#22c55e';
            iconClass = 'fa-handshake';
        } else if (type === 'investment') {
            color = '#f59e0b';
            iconClass = 'fa-coins';
        } else {
            color = '#8b5cf6';
            iconClass = 'fa-globe';
        }
        return L.divIcon({
            className: 'custom-div-icon',
            html: `<div style="background:${color};width:36px;height:36px;border-radius:50%;display:flex;align-items:center;justify-content:center;border:2px solid #fff;box-shadow:0 2px 12px rgba(0,0,0,0.15);transition:transform 0.2s;">
                    <i class="fas ${iconClass}" style="color:#fff;font-size:16px;"></i>
                   </div>`,
            iconSize: [36, 36],
            iconAnchor: [18, 18],
            popupAnchor: [0, -18]
        });
    }

    function createPopupContent(country) {
        // Graphique de progression (simulé)
        const projectBar = Math.min((country.projects / 5) * 100, 100);
        const partnerBar = Math.min((country.partners / 3) * 100, 100);
        const investBar = Math.min((parseInt(country.investment) / 5) * 100, 100);

        return `
            <div class="country-popup" style="min-width:220px;font-family:'Inter',sans-serif;">
                <div class="country-name" style="font-size:1.1rem;font-weight:700;display:flex;align-items:center;gap:8px;margin-bottom:8px;">
                    <span style="font-size:1.4rem;">${country.flag || '🌍'}</span>
                    ${country.name}
                </div>
                <div class="country-detail" style="font-size:0.85rem;color:var(--text-secondary);">
                    <strong>Capitale :</strong> ${country.capital || '—'}
                </div>
                <div style="margin:8px 0;">
                    <div style="display:flex;justify-content:space-between;font-size:0.8rem;">
                        <span>📊 Projets <strong>${country.projects}</strong></span>
                        <span>🤝 Partenaires <strong>${country.partners}</strong></span>
                        <span>💰 Invest. <strong>${country.investment || '—'}</strong></span>
                    </div>
                    <div style="background:var(--bg-secondary);border-radius:4px;height:4px;margin-top:4px;overflow:hidden;">
                        <div style="background:#3b82f6;width:${projectBar}%;height:4px;border-radius:4px;float:left;"></div>
                        <div style="background:#22c55e;width:${partnerBar}%;height:4px;border-radius:4px;float:left;"></div>
                        <div style="background:#f59e0b;width:${investBar}%;height:4px;border-radius:4px;float:left;"></div>
                    </div>
                </div>
                <div style="margin-top:8px;">
                    <a href="/projects?country=${encodeURIComponent(country.name)}"
                       style="color:var(--primary-blue);text-decoration:none;font-size:0.8rem;font-weight:500;padding:2px 12px;border:1px solid var(--primary-blue);border-radius:50px;display:inline-block;transition:all 0.2s;"
                       onmouseover="this.style.background='var(--primary-blue)';this.style.color='#fff';"
                       onmouseout="this.style.background='transparent';this.style.color='var(--primary-blue)';">
                        Voir les projets →
                    </a>
                </div>
            </div>
        `;
    }

    function addMarker(country) {
        const icon = createIcon(country.type || 'project');
        const marker = L.marker([country.lat, country.lng], { icon: icon })
            .addTo(map)
            .bindPopup(createPopupContent(country), { maxWidth: 280, className: 'custom-popup' });

        // Stocker le marqueur avec ses métadonnées pour le filtrage
        marker._type = country.type || 'project';
        marker._country = country.name;
        allMarkers.push(marker);
        return marker;
    }

    // === LIGNES DE CONNEXION (Import-Export) ===
    function addConnections() {
        console.log('🔗 Ajout des lignes de connexion...');
        const connections = [
            { from: [12.65, -8.0], to: [14.5, -14.5], label: '🛒 Export coton', color: '#f59e0b' },
            { from: [12.65, -8.0], to: [48.9, 2.3], label: '📦 Import machines', color: '#3b82f6' },
            { from: [12.65, -8.0], to: [40.7, -74.0], label: '🚢 Export produits', color: '#22c55e' },
            { from: [14.5, -14.5], to: [6.8, -5.3], label: '🌾 Échanges agricoles', color: '#8b5cf6' }
        ];

        connections.forEach(conn => {
            const polyline = L.polyline([conn.from, conn.to], {
                color: conn.color,
                weight: 2,
                opacity: 0.5,
                dashArray: '6, 8',
                smoothFactor: 1
            }).addTo(map);

            // Animation au survol
            polyline.on('mouseover', function() {
                this.setStyle({ weight: 4, opacity: 0.9 });
                this._map._container.style.cursor = 'pointer';
            });
            polyline.on('mouseout', function() {
                this.setStyle({ weight: 2, opacity: 0.5 });
                this._map._container.style.cursor = '';
            });

            // Popup sur la ligne
            const midLat = (conn.from[0] + conn.to[0]) / 2;
            const midLng = (conn.from[1] + conn.to[1]) / 2;
            L.marker([midLat, midLng], {
                icon: L.divIcon({
                    className: 'connection-label',
                    html: `<div style="background:rgba(0,0,0,0.6);color:#fff;padding:2px 10px;border-radius:50px;font-size:0.7rem;white-space:nowrap;box-shadow:0 2px 8px rgba(0,0,0,0.15);">${conn.label}</div>`,
                    iconSize: [0, 0]
                })
            }).addTo(map);
        });
    }

    // === FILTRES INTERACTIFS ===
    function setupFilters() {
        console.log('🎯 Configuration des filtres...');
        const filterButtons = document.querySelectorAll('.filter-btn');
        filterButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                const type = this.dataset.type;
                currentFilter = type;
                // Mise à jour visuelle des boutons
                filterButtons.forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                applyFilter(type);
            });
        });
        // Activer "Tous" par défaut
        const defaultBtn = document.querySelector('.filter-btn[data-type="all"]');
        if (defaultBtn) defaultBtn.classList.add('active');
    }

    function applyFilter(type) {
        console.log(`🔍 Application du filtre : ${type}`);
        allMarkers.forEach(marker => {
            const markerType = marker._type || 'project';
            if (type === 'all' || markerType === type) {
                if (!map.hasLayer(marker)) {
                    marker.addTo(map);
                }
            } else {
                if (map.hasLayer(marker)) {
                    map.removeLayer(marker);
                }
            }
        });
    }

    // === AJOUT DES MARQUEURS ===
    console.log('📍 Ajout des marqueurs...');
    countryData.forEach(country => {
        addMarker(country);
    });

    // === AJOUT DES CONNEXIONS ===
    addConnections();

    // === AJOUT DES FILTRES ===
    setupFilters();

    // === AJUSTEMENT DE LA VUE ===
    const bounds = countryData.map(c => [c.lat, c.lng]);
    map.fitBounds(bounds, { padding: [50, 50], maxZoom: 4 });

    // === GESTION DE REDIMENSIONNEMENT ===
    window.addEventListener('resize', function() {
        map.invalidateSize();
    });

    console.log('✅ Carte premium initialisée avec succès !');
    console.log(`📍 ${allMarkers.length} marqueurs, ${countryData.length} pays, ${4} connexions.`);
});