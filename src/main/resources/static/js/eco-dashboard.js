// ============================================
// ÉCO-RESPONSABILITÉ - TABLEAU DE BORD
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 eco-dashboard.js chargé');

    // === CHARGEMENT DES DONNÉES ===
    let indicators = {};
    let historyData = [];
    let tipsData = [];

    function loadData() {
        // Indicateurs
        fetch('/api/eco/indicators')
            .then(res => res.json())
            .then(data => {
                indicators = data;
                renderKPIs(data);
                renderGoals(data.goals);
                renderCertifications(data.certifications);
                renderCarbonBreakdown(data.carbon.breakdown);
            })
            .catch(err => console.error('Erreur indicateurs:', err));

        // Historique
        fetch('/api/eco/history')
            .then(res => res.json())
            .then(data => {
                historyData = data;
                renderCarbonHistory(data);
                renderWaterHistory(data);
                renderEnergyHistory(data);
            })
            .catch(err => console.error('Erreur historique:', err));

        // Conseils
        fetch('/api/eco/tips')
            .then(res => res.json())
            .then(data => {
                tipsData = data;
                renderTips(data);
            })
            .catch(err => console.error('Erreur conseils:', err));
    }

    // === RENDU DES KPI ===
    function renderKPIs(data) {
        const container = document.getElementById('kpiCards');
        const kpis = [
            { key: 'carbon', icon: 'fa-cloud', label: 'Empreinte carbone (t CO₂)', value: data.carbon.total, target: data.carbon.target, trend: data.carbon.trend },
            { key: 'water', icon: 'fa-tint', label: 'Consommation d\'eau (m³)', value: data.water.total, target: data.water.target, trend: data.water.trend },
            { key: 'energy', icon: 'fa-bolt', label: 'Énergie (MWh)', value: data.energy.total, target: data.energy.target, trend: data.energy.trend },
            { key: 'recycling', icon: 'fa-recycle', label: 'Taux de recyclage (%)', value: data.recycling.total, target: data.recycling.target, trend: data.recycling.trend }
        ];

        container.innerHTML = kpis.map(kpi => {
            const trendClass = kpi.trend < 0 ? 'positive' : (kpi.trend > 0 ? (kpi.key === 'recycling' ? 'positive' : 'negative') : 'neutral');
            const trendSign = kpi.trend > 0 ? '+' : '';
            const trendDisplay = kpi.trend !== undefined ? `${trendSign}${kpi.trend.toFixed(1)}%` : '—';
            const isGoodTrend = (kpi.key === 'recycling' && kpi.trend > 0) || (kpi.key !== 'recycling' && kpi.trend < 0);
            const finalTrendClass = isGoodTrend ? 'positive' : (kpi.trend !== 0 ? 'negative' : 'neutral');

            return `
                <div class="col-6 col-md-3">
                    <div class="kpi-card" data-aos="fade-up" data-aos-delay="100">
                        <div class="kpi-icon"><i class="fas ${kpi.icon}"></i></div>
                        <div class="kpi-value">${kpi.value}</div>
                        <div class="kpi-label">${kpi.label}</div>
                        <div class="kpi-trend ${finalTrendClass}">${trendDisplay} vs objectif</div>
                        <small style="color:var(--text-muted);font-size:0.65rem;">Objectif: ${kpi.target}</small>
                    </div>
                </div>
            `;
        }).join('');
    }

    // === GRAPHIQUE : Évolution carbone ===
    let carbonChart = null;
    function renderCarbonHistory(data) {
        const ctx = document.getElementById('carbonHistoryChart').getContext('2d');
        if (carbonChart) carbonChart.destroy();

        carbonChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(d => d.month),
                datasets: [{
                    label: 'Empreinte carbone (t CO₂)',
                    data: data.map(d => Math.round(d.carbon * 10) / 10),
                    borderColor: '#3b82f6',
                    backgroundColor: 'rgba(59,130,246,0.08)',
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#3b82f6',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: { color: 'var(--text-secondary)', font: { size: 11 } }
                    }
                },
                scales: {
                    y: {
                        grid: { color: 'rgba(148,163,184,0.08)' },
                        ticks: { color: 'var(--text-muted)' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: 'var(--text-muted)', maxTicksLimit: 8 }
                    }
                }
            }
        });
    }

    // === GRAPHIQUE : Évolution eau ===
    let waterChart = null;
    function renderWaterHistory(data) {
        const ctx = document.getElementById('waterHistoryChart').getContext('2d');
        if (waterChart) waterChart.destroy();

        waterChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(d => d.month),
                datasets: [{
                    label: 'Consommation d\'eau (m³)',
                    data: data.map(d => Math.round(d.water / 100) * 100),
                    borderColor: '#22c55e',
                    backgroundColor: 'rgba(34,197,94,0.08)',
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#22c55e',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: { color: 'var(--text-secondary)', font: { size: 11 } }
                    }
                },
                scales: {
                    y: {
                        grid: { color: 'rgba(148,163,184,0.08)' },
                        ticks: { color: 'var(--text-muted)' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: 'var(--text-muted)', maxTicksLimit: 8 }
                    }
                }
            }
        });
    }

    // === GRAPHIQUE : Évolution énergie ===
    let energyChart = null;
    function renderEnergyHistory(data) {
        const ctx = document.getElementById('energyHistoryChart').getContext('2d');
        if (energyChart) energyChart.destroy();

        // On simule une énergie qui suit la tendance carbone
        const energyData = data.map(d => Math.round((d.carbon * 1.4 + 10) * 10) / 10);

        energyChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(d => d.month),
                datasets: [{
                    label: 'Consommation d\'énergie (MWh)',
                    data: energyData,
                    borderColor: '#f59e0b',
                    backgroundColor: 'rgba(245,158,11,0.08)',
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#f59e0b',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: { color: 'var(--text-secondary)', font: { size: 11 } }
                    }
                },
                scales: {
                    y: {
                        grid: { color: 'rgba(148,163,184,0.08)' },
                        ticks: { color: 'var(--text-muted)' }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { color: 'var(--text-muted)', maxTicksLimit: 8 }
                    }
                }
            }
        });
    }

    // === GRAPHIQUE : Répartition carbone ===
    let breakdownChart = null;
    function renderCarbonBreakdown(breakdown) {
        const ctx = document.getElementById('carbonBreakdownChart').getContext('2d');
        if (breakdownChart) breakdownChart.destroy();

        const labels = Object.keys(breakdown);
        const values = Object.values(breakdown);
        const colors = ['#3b82f6', '#22c55e', '#f59e0b'];

        breakdownChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: values,
                    backgroundColor: colors,
                    borderColor: 'var(--bg-card)',
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { color: 'var(--text-secondary)', font: { size: 11 }, padding: 12 }
                    }
                }
            }
        });
    }

    // === OBJECTIFS RSE ===
    function renderGoals(goals) {
        const container = document.getElementById('goalsContainer');
        const entries = Object.entries(goals);
        const labels = {
            neutraliteCarbone: 'Neutralité Carbone',
            eauResponsable: 'Eau Responsable',
            dechetsZero: 'Zéro Déchet',
            energieVerte: 'Énergie Verte'
        };
        const colors = {
            neutraliteCarbone: '#3b82f6',
            eauResponsable: '#22c55e',
            dechetsZero: '#f59e0b',
            energieVerte: '#8b5cf6'
        };

        container.innerHTML = entries.map(([key, value]) => `
            <div class="goal-item">
                <span class="goal-label">${labels[key] || key}</span>
                <div class="goal-bar">
                    <div class="goal-fill" style="width:${value}%;background:${colors[key] || '#3b82f6'};"></div>
                </div>
                <span class="goal-percent">${value}%</span>
            </div>
        `).join('');
    }

    // === CERTIFICATIONS ===
    function renderCertifications(certs) {
        const container = document.getElementById('certificationsContainer');
        container.innerHTML = certs.map(cert => `
            <span class="cert-badge"><i class="fas fa-certificate"></i> ${cert}</span>
        `).join('');
    }

    // === CONSEILS ===
    function renderTips(tips) {
        const container = document.getElementById('tipsContainer');
        container.innerHTML = tips.map(tip => `
            <div class="tip-item">
                <div class="tip-title">${tip.title}</div>
                <div class="tip-desc">${tip.desc}</div>
            </div>
        `).join('');
    }

    // === INIT ===
    loadData();

    // Recharger périodiquement (toutes les 5 minutes)
    setInterval(() => {
        loadData();
        console.log('🔄 Données éco-responsabilité actualisées');
    }, 300000);

    console.log('✅ Éco-dashboard initialisé');
});