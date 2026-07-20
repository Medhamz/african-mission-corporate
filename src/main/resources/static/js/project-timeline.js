// ============================================
// PROJECT TIMELINE GANTT AVEC MÉTÉO
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const container = document.getElementById('ganttChart');
    if (!container) return;

    // Fonction pour récupérer les données projets
    async function fetchTimeline() {
        try {
            const res = await fetch('/api/projects/timeline');
            if (!res.ok) throw new Error('Erreur HTTP ' + res.status);
            return await res.json();
        } catch (e) {
            console.error('Erreur chargement projets :', e);
            return [];
        }
    }

    // Fonction pour récupérer la météo d'un lieu (via votre API)
    async function fetchWeatherForLocation(location) {
        try {
            // On utilise l'endpoint météo existant pour Bamako, on pourrait étendre pour d'autres villes
            const res = await fetch('/api/market/weather');
            if (!res.ok) throw new Error('Erreur météo');
            const data = await res.json();
            // On simule des jours de pluie en fonction de la description
            const desc = data.weather?.[0]?.description?.toLowerCase() || '';
            const rainDays = desc.includes('rain') || desc.includes('pluie') ? 2 : (desc.includes('cloud') ? 1 : 0);
            return { rainDays };
        } catch (e) {
            console.warn('Erreur météo :', e);
            return { rainDays: 0 };
        }
    }

    // Fonction pour dessiner le Gantt
    function renderGantt(projects) {
        if (!projects || projects.length === 0) {
            container.innerHTML = '<div class="gantt-loading"><i class="fas fa-exclamation-circle"></i>Aucun projet trouvé</div>';
            return;
        }

        // Trouver les dates min et max pour l'échelle
        const today = new Date();
        let minDate = new Date(projects[0].start);
        let maxDate = new Date(projects[0].end);
        projects.forEach(p => {
            const s = new Date(p.start);
            const e = new Date(p.end);
            if (s < minDate) minDate = s;
            if (e > maxDate) maxDate = e;
        });
        // Ajouter une marge de 10% avant et après
        const range = maxDate.getTime() - minDate.getTime();
        const margin = range * 0.1;
        const startMs = minDate.getTime() - margin;
        const endMs = maxDate.getTime() + margin;
        const totalMs = endMs - startMs;

        // Générer les ticks (mois)
        const months = [];
        let current = new Date(startMs);
        while (current.getTime() < endMs) {
            months.push(new Date(current));
            current.setMonth(current.getMonth() + 1);
        }

        // Créer le HTML
        let html = `<div class="gantt-grid">`;
        // Ligne des ticks
        html += `<div class="gantt-ticks">`;
        months.forEach((m, idx) => {
            const label = m.toLocaleDateString('fr-FR', { month: 'short', year: 'numeric' });
            const left = (m.getTime() - startMs) / totalMs * 100;
            html += `<div class="gantt-tick" style="flex:0 0 ${100/months.length}%;">${label}</div>`;
        });
        html += `</div>`;

        // Barres pour chaque projet
        projects.forEach(project => {
            const start = new Date(project.start);
            const end = new Date(project.end);
            const left = (start.getTime() - startMs) / totalMs * 100;
            const width = (end.getTime() - start.getTime()) / totalMs * 100;

            // Déterminer la classe de statut
            let statusClass = 'status-prevu';
            if (project.status === 'En cours') statusClass = 'status-en-cours';
            else if (project.status === 'À venir') statusClass = 'status-a-venir';
            else if (project.status === 'En retard') statusClass = 'status-retard';

            // Récupération des jours de pluie estimés (simulé)
            const rainDays = project.rainDays || 0;
            const rainIcon = rainDays > 0 ? `<span class="gantt-rain-icon"><i class="fas fa-cloud-rain"></i> ${rainDays}j</span>` : '';

            html += `<div class="gantt-row">`;
            html += `<div class="gantt-label" title="${project.name}">${project.name}</div>`;
            html += `<div class="gantt-bar-wrapper">`;
            html += `<div class="gantt-bar ${statusClass}" style="left:${left}%;width:${Math.max(width, 2)}%;">${project.name} ${rainIcon}</div>`;
            html += `</div>`;
            html += `</div>`;
        });

        html += `</div>`;

        // Ajouter un résumé météo
        const totalRainDays = projects.reduce((sum, p) => sum + (p.rainDays || 0), 0);
        if (totalRainDays > 0) {
            html += `<div class="gantt-weather-info">`;
            html += `<span class="rain-days"><i class="fas fa-cloud-rain"></i> ${totalRainDays} jours de pluie estimés sur la période</span>`;
            html += `<span>⚠️ Les délais peuvent être ajustés en fonction des prévisions.</span>`;
            html += `</div>`;
        }

        container.innerHTML = html;
    }

    // Chargement et affichage
    async function loadTimeline() {
        container.innerHTML = '<div class="gantt-loading"><i class="fas fa-spinner"></i> Chargement du planning...</div>';

        const projects = await fetchTimeline();
        if (!projects.length) {
            container.innerHTML = '<div class="gantt-loading"><i class="fas fa-exclamation-circle"></i> Aucun projet à afficher.</div>';
            return;
        }

        // Pour chaque projet, récupérer la météo (simulée ici avec un délai)
        // On utilise une promesse pour simuler l'appel météo (ou utiliser l'API réelle)
        const weatherPromises = projects.map(async (p) => {
            const weather = await fetchWeatherForLocation(p.location || 'Bamako');
            p.rainDays = weather.rainDays || 0;
            // Si pluie, ajuster le statut éventuellement
            if (p.rainDays > 0 && p.status === 'En cours') {
                p.status = 'En retard';
            }
            return p;
        });

        const projectsWithWeather = await Promise.all(weatherPromises);
        renderGantt(projectsWithWeather);
    }

    loadTimeline();
});