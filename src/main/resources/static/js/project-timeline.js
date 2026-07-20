// ============================================
// PROJECT TIMELINE GANTT AVEC MÉTÉO
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 project-timeline.js chargé');

    const container = document.getElementById('ganttChart');
    if (!container) {
        console.warn('⚠️ #ganttChart introuvable');
        return;
    }

    // Fonction pour récupérer les données projets
    async function fetchTimeline() {
        try {
            const res = await fetch('/api/projects/timeline');
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const data = await res.json();
            console.log('📦 Projets reçus :', data);
            return data;
        } catch (e) {
            console.error('❌ Erreur chargement projets :', e);
            return [];
        }
    }

    // Fonction pour dessiner le Gantt
    function renderGantt(projects) {
        console.log('📊 Rendu Gantt avec', projects.length, 'projets');

        if (!projects || projects.length === 0) {
            container.innerHTML = '<div class="gantt-loading"><i class="fas fa-exclamation-circle"></i> Aucun projet à afficher.</div>';
            return;
        }

        // Trouver les dates min et max
        let minDate = new Date(projects[0].start);
        let maxDate = new Date(projects[0].end);
        projects.forEach(p => {
            const s = new Date(p.start);
            const e = new Date(p.end);
            if (s < minDate) minDate = s;
            if (e > maxDate) maxDate = e;
        });
        const range = maxDate.getTime() - minDate.getTime();
        const margin = range * 0.1 || 30 * 24 * 60 * 60 * 1000; // 30 jours si range nul
        const startMs = minDate.getTime() - margin;
        const endMs = maxDate.getTime() + margin;
        const totalMs = endMs - startMs;

        // Ticks (mois)
        const months = [];
        let current = new Date(startMs);
        while (current.getTime() < endMs) {
            months.push(new Date(current));
            current.setMonth(current.getMonth() + 1);
        }

        let html = `<div class="gantt-grid">`;

        // Ticks
        html += `<div class="gantt-ticks">`;
        months.forEach((m) => {
            const label = m.toLocaleDateString('fr-FR', { month: 'short', year: 'numeric' });
            html += `<div class="gantt-tick" style="flex:0 0 ${100/months.length}%;">${label}</div>`;
        });
        html += `</div>`;

        // Barres
        projects.forEach(project => {
            const start = new Date(project.start);
            const end = new Date(project.end);
            const left = (start.getTime() - startMs) / totalMs * 100;
            const width = (end.getTime() - start.getTime()) / totalMs * 100;

            // Statut
            let statusClass = 'status-prevu';
            if (project.status === 'En cours') statusClass = 'status-en-cours';
            else if (project.status === 'À venir') statusClass = 'status-a-venir';
            else if (project.status === 'En retard') statusClass = 'status-retard';

            const rainDays = project.rainDays || 0;
            const rainIcon = rainDays > 0 ? `<span class="gantt-rain-icon"><i class="fas fa-cloud-rain"></i> ${rainDays}j</span>` : '';

            html += `<div class="gantt-row">`;
            html += `<div class="gantt-label" title="${project.name}">${project.name}</div>`;
            html += `<div class="gantt-bar-wrapper">`;
            html += `<div class="gantt-bar ${statusClass}" style="left:${Math.min(left, 98)}%;width:${Math.max(width, 2)}%;">${project.name} ${rainIcon}</div>`;
            html += `</div>`;
            html += `</div>`;
        });

        html += `</div>`;

        // Résumé météo
        const totalRainDays = projects.reduce((sum, p) => sum + (p.rainDays || 0), 0);
        if (totalRainDays > 0) {
            html += `<div class="gantt-weather-info">`;
            html += `<span class="rain-days"><i class="fas fa-cloud-rain"></i> ${totalRainDays} jours de pluie estimés sur la période</span>`;
            html += `<span>⚠️ Les délais peuvent être ajustés en fonction des prévisions.</span>`;
            html += `</div>`;
        }

        container.innerHTML = html;
        console.log('✅ Gantt rendu');
    }

    // Chargement
    async function loadTimeline() {
        container.innerHTML = '<div class="gantt-loading"><i class="fas fa-spinner"></i> Chargement du planning...</div>';

        const projects = await fetchTimeline();

        if (!projects || projects.length === 0) {
            container.innerHTML = '<div class="gantt-loading"><i class="fas fa-exclamation-circle"></i> Aucun projet à afficher.</div>';
            return;
        }

        renderGantt(projects);
    }

    loadTimeline();
});