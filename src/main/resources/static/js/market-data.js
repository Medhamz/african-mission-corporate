// ============================================
// CHARGEMENT DONNÉES MÉTÉO & MARCHÉS
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('marketData');
    if (!container) return;

    // Fonction pour mettre à jour les données
    function fetchMarketData() {
        // Météo
        fetch('/api/market/weather')
            .then(res => res.json())
            .then(data => {
                const cityEl = document.getElementById('weatherCity');
                const tempEl = document.getElementById('weatherTemp');
                const descEl = document.getElementById('weatherDesc');

                if (cityEl && data.name) {
                    cityEl.textContent = data.name;
                }
                if (tempEl && data.main && data.main.temp) {
                    tempEl.textContent = Math.round(data.main.temp) + '°C';
                }
                if (descEl && data.weather && data.weather[0]) {
                    descEl.textContent = data.weather[0].description;
                }

                // Icône météo (simulée)
                const iconEl = document.querySelector('.weather-icon');
                if (iconEl) {
                    const desc = data.weather && data.weather[0] ? data.weather[0].description.toLowerCase() : '';
                    if (desc.includes('pluie') || desc.includes('rain')) {
                        iconEl.className = 'fas fa-cloud-rain weather-icon';
                    } else if (desc.includes('nuage') || desc.includes('cloud')) {
                        iconEl.className = 'fas fa-cloud weather-icon';
                    } else {
                        iconEl.className = 'fas fa-cloud-sun weather-icon';
                    }
                }
            })
            .catch(err => console.warn('Erreur météo :', err));

        // Marchés
        fetch('/api/market/prices')
            .then(res => res.json())
            .then(data => {
                if (!Array.isArray(data)) return;
                data.forEach(item => {
                    const el = document.getElementById('market' + item.symbol);
                    if (!el) return;
                    const change = parseFloat(item.change);
                    const sign = change >= 0 ? '+' : '';
                    const colorClass = change >= 0 ? 'up' : 'down';
                    el.innerHTML = `
                        ${item.symbol}: ${item.price}
                        <span class="${colorClass}">${sign}${item.change} (${item.changePercent})</span>
                    `;
                });
            })
            .catch(err => console.warn('Erreur marchés :', err));
    }

    // Charger immédiatement
    fetchMarketData();
    // Mettre à jour toutes les 5 minutes
    setInterval(fetchMarketData, 300000);

    // Afficher la section
    container.style.display = 'block';
});