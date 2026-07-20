// ============================================
// DIAGNOSTIQUEUR INTELLIGENT
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const steps = document.querySelectorAll('.step');
    const optionBtns = document.querySelectorAll('.option-btn');
    const prevBtn = document.getElementById('prevStep');
    const nextBtn = document.getElementById('nextStep');
    const resetBtn = document.getElementById('resetDiagnostic');

    let currentStep = 1;
    let answers = {};

    // Fonction pour afficher une étape
    function showStep(step) {
        steps.forEach(s => s.classList.remove('active'));
        const target = document.querySelector(`.step[data-step="${step}"]`);
        if (target) target.classList.add('active');

        prevBtn.disabled = step === 1;
        if (step === 5) {
            nextBtn.textContent = 'Analyser';
        } else {
            nextBtn.textContent = 'Suivant';
        }

        currentStep = step;
    }

    // Fonction pour collecter la réponse d'une étape
    function getAnswerForStep(step) {
        const selected = document.querySelector(`.step[data-step="${step}"] .option-btn.selected`);
        return selected ? selected.dataset.value : null;
    }

    // Gestion des clics sur les options
    optionBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const parentStep = this.closest('.step');
            const step = parseInt(parentStep.dataset.step);
            // Désélectionner les autres dans cette étape
            parentStep.querySelectorAll('.option-btn').forEach(b => b.classList.remove('selected'));
            this.classList.add('selected');
            // Stocker la réponse
            answers[step] = this.dataset.value;
            // Activer le bouton suivant
            nextBtn.disabled = false;
        });
    });

    // Bouton Suivant
    nextBtn.addEventListener('click', function() {
        if (this.textContent === 'Analyser') {
            // Vérifier que toutes les réponses sont remplies
            for (let i = 1; i <= 5; i++) {
                if (!answers[i]) {
                    alert('Veuillez répondre à toutes les questions.');
                    return;
                }
            }
            generateResult();
            return;
        }

        const next = currentStep + 1;
        if (next <= 5) {
            showStep(next);
            // Vérifier si cette étape a déjà une réponse
            if (answers[next]) {
                const selected = document.querySelector(`.step[data-step="${next}"] .option-btn[data-value="${answers[next]}"]`);
                if (selected) selected.classList.add('selected');
                // Activer le bouton suivant
                nextBtn.disabled = false;
            } else {
                nextBtn.disabled = true;
            }
        }
    });

    // Bouton Précédent
    prevBtn.addEventListener('click', function() {
        if (currentStep > 1) {
            showStep(currentStep - 1);
            nextBtn.disabled = false;
        }
    });

    // Fonction pour générer le résultat (simulé avec données météo / marchés)
    async function generateResult() {
        const resultDiv = document.getElementById('diagnosticResult');
        const formDiv = document.getElementById('diagnosticForm');

        // Afficher un loading
        resultDiv.style.display = 'block';
        resultDiv.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin" style="font-size:2rem;color:var(--primary-blue);"></i><p>Analyse en cours...</p></div>';

        // Récupérer les données météo et marchés (via vos API)
        try {
            const [weatherRes, marketRes] = await Promise.all([
                fetch('/api/market/weather'),
                fetch('/api/market/prices')
            ]);
            const weather = await weatherRes.json();
            const markets = await marketRes.json();

            // Simuler un calcul basé sur les réponses
            const projectType = answers[1];
            const budget = answers[2];
            const location = answers[3];
            const duration = answers[4];
            const priority = answers[5];

            // Estimation délai (simulé)
            let baseDuration = 0;
            if (duration.includes('3 mois')) baseDuration = 2;
            else if (duration.includes('6 mois')) baseDuration = 5;
            else if (duration.includes('12 mois')) baseDuration = 10;
            else baseDuration = 15;

            // Ajustement météo (pluie)
            let rainDays = 0;
            if (weather && weather.weather && weather.weather[0]) {
                const desc = weather.weather[0].description.toLowerCase();
                if (desc.includes('rain') || desc.includes('pluie')) rainDays = 2;
                else if (desc.includes('cloud')) rainDays = 1;
            }
            const totalDuration = baseDuration + rainDays;

            // Estimation coût (simulé)
            let cost = 0;
            if (budget.includes('< 50k')) cost = 30 + Math.random() * 20;
            else if (budget.includes('50k - 200k')) cost = 120 + Math.random() * 80;
            else if (budget.includes('200k - 500k')) cost = 350 + Math.random() * 150;
            else cost = 600 + Math.random() * 300;

            // Ajustement marché (prix matières)
            let marketImpact = 0;
            if (markets && Array.isArray(markets)) {
                const steel = markets.find(m => m.symbol === 'STEEL');
                if (steel) {
                    const price = parseFloat(steel.price);
                    if (price > 700) marketImpact = 1.1;
                    else if (price > 650) marketImpact = 1.05;
                    else marketImpact = 0.95;
                }
            }
            const totalCost = Math.round(cost * marketImpact);

            // Score de risque (simulé)
            let riskScore = 0;
            let riskLabel = '';
            let recommendations = '';

            if (projectType === 'btp') riskScore = 30 + Math.random() * 20;
            else if (projectType === 'agriculture') riskScore = 20 + Math.random() * 20;
            else if (projectType === 'import-export') riskScore = 40 + Math.random() * 20;
            else riskScore = 15 + Math.random() * 15;

            // Ajustement météo et marché
            riskScore += rainDays * 5;
            riskScore += (marketImpact > 1.05) ? 5 : 0;

            riskScore = Math.min(100, Math.max(10, riskScore));

            if (riskScore < 30) {
                riskLabel = '🟢 Faible risque';
                recommendations = 'Projet bien dimensionné. Optimisez les délais.';
            } else if (riskScore < 60) {
                riskLabel = '🟡 Risque modéré';
                recommendations = 'Surveillez la météo et les prix des matières.';
            } else {
                riskLabel = '🔴 Risque élevé';
                recommendations = 'Prévoyez des marges de sécurité et un suivi rapproché.';
            }

            // Afficher les résultats
            resultDiv.innerHTML = `
                <h3 class="text-center"><span class="gradient-text">Analyse</span> de votre projet</h3>
                <div class="result-grid">
                    <div class="result-card">
                        <i class="fas fa-calendar-alt"></i>
                        <h5>Délai estimé</h5>
                        <p>${totalDuration} mois</p>
                        <small>(impact météo : +${rainDays} jours)</small>
                    </div>
                    <div class="result-card">
                        <i class="fas fa-euro-sign"></i>
                        <h5>Coût estimé</h5>
                        <p>${totalCost} k€</p>
                        <small>(impact prix matières : ${Math.round((marketImpact - 1) * 100)}%)</small>
                    </div>
                    <div class="result-card">
                        <i class="fas fa-shield-alt"></i>
                        <h5>Score de risque</h5>
                        <p>${Math.round(riskScore)}%</p>
                        <small>${riskLabel}</small>
                    </div>
                    <div class="result-card">
                        <i class="fas fa-lightbulb"></i>
                        <h5>Recommandations</h5>
                        <p>${recommendations}</p>
                    </div>
                </div>
                <div class="chart-container mt-4">
                    <canvas id="radarChart" width="400" height="300"></canvas>
                </div>
                <div class="text-center mt-4">
                    <button id="resetDiagnostic" class="btn btn-outline-primary">Refaire le diagnostic</button>
                </div>
            `;

            // Créer le graphique radar
            const ctx = document.getElementById('radarChart').getContext('2d');
            new Chart(ctx, {
                type: 'radar',
                data: {
                    labels: ['Délai', 'Coût', 'Qualité', 'Durabilité', 'Risque'],
                    datasets: [{
                        label: 'Votre projet',
                        data: [
                            100 - totalDuration / 15 * 100,
                            100 - totalCost / 1000 * 100,
                            priority === 'qualite' ? 90 : 50 + Math.random() * 30,
                            priority === 'durabilite' ? 90 : 40 + Math.random() * 30,
                            100 - riskScore
                        ],
                        backgroundColor: 'rgba(59, 130, 246, 0.2)',
                        borderColor: '#3b82f6',
                        pointBackgroundColor: '#3b82f6',
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    scales: {
                        r: {
                            beginAtZero: true,
                            max: 100,
                            grid: { color: 'rgba(255,255,255,0.05)' },
                            pointLabels: { color: 'var(--text-secondary)' }
                        }
                    },
                    plugins: {
                        legend: { labels: { color: 'var(--text-primary)' } }
                    }
                }
            });

            // Réinitialiser
            document.getElementById('resetDiagnostic').addEventListener('click', resetDiagnostic);

        } catch (error) {
            console.error('Erreur diagnostic :', error);
            resultDiv.innerHTML = `
                <div class="text-center">
                    <i class="fas fa-exclamation-triangle" style="font-size:2rem;color:#ef4444;"></i>
                    <p>Erreur lors de l'analyse. Veuillez réessayer.</p>
                    <button id="resetDiagnostic" class="btn btn-outline-primary">Réessayer</button>
                </div>
            `;
            document.getElementById('resetDiagnostic')?.addEventListener('click', resetDiagnostic);
        }
    }

    function resetDiagnostic() {
        document.getElementById('diagnosticResult').style.display = 'none';
        document.getElementById('diagnosticForm').style.display = 'block';
        // Réinitialiser les réponses
        answers = {};
        document.querySelectorAll('.option-btn').forEach(b => b.classList.remove('selected'));
        document.querySelectorAll('.step').forEach(s => s.classList.remove('active'));
        showStep(1);
        nextBtn.disabled = true;
        // Afficher le formulaire
        document.getElementById('diagnosticForm').style.display = 'block';
    }

    // Initialiser
    showStep(1);
    nextBtn.disabled = true;
});