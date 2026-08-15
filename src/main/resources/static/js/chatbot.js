// ============================================
// CHATBOT WIDGET - VERSION INTELLIGENTE ET AMÉLIORÉE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('chatbotToggle');
    const closeBtn = document.getElementById('chatbotClose');
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const sendBtn = document.getElementById('chatbotSend');
    const messagesEl = document.getElementById('chatbotMessages');

    // ============================================
    // BASE DE CONNAISSANCES & INTENTIONS ENRICHIES
    // ============================================
    const intentions = [
        {
            id: 'salutation',
            keywords: ['bonjour', 'salut', 'hello', 'coucou', 'bonsoir', 'hey', 'bienvenue'],
            response: 'Bonjour ! Comment puis-je vous aider aujourd\'hui ?'
        },
        {
            id: 'services_generaux',
            keywords: ['service', 'activité', 'prestation', 'secteur', 'faires', 'metier', 'domaine', 'proposer', 'offre'],
            response: 'Nous sommes une entreprise polyvalente intervenant dans plusieurs domaines majeurs :<br>• 🏢 <strong>Promotion Immobilière & BTP</strong><br>• 🌾 <strong>Agrobusiness</strong><br>• 📦 <strong>Import-Export</strong><br>• ♻️ <strong>Développement Durable</strong><br>Découvrez l\'ensemble sur notre page <a href="/activities" target="_blank">Nos Activités</a>.'
        },
        {
            id: 'devis_tarif',
            keywords: ['devis', 'tarif', 'prix', 'cout', 'coût', 'facture', 'estimation', 'payer', 'combien', 'budget'],
            response: 'Pour obtenir une estimation précise ou un devis personnalisé, vous pouvez nous faire une demande via notre <a href="/contact" target="_blank">Formulaire de contact</a> ou nous appeler directement au <strong>+223 44 39 12 03</strong>.'
        },
        {
            id: 'contact_info',
            keywords: ['contact', 'telephone', 'téléphone', 'email', 'mail', 'joindre', 'adresse', 'emplacement', 'situé', 'situer', 'ou', 'siège', 'bureau', 'bamako', 'mali'],
            response: '📍 <strong>Adresse :</strong> Bamako, Mali<br>📞 <strong>Téléphone :</strong> +223 44 39 12 03<br>✉️ <strong>Email :</strong> contact@africanmission.com'
        },
        {
            id: 'horaires',
            keywords: ['heure', 'horaire', 'ouverture', 'fermeture', 'quand', 'ouvert', 'disponibilité', 'jour'],
            response: 'Nos bureaux sont ouverts du <strong>Lundi au Vendredi</strong> de <strong>08h00 à 17h00</strong>.'
        },
        {
            id: 'projets',
            keywords: ['projet', 'realisation', 'réalisation', 'chantier', 'client', 'travaux', 'portfolio', 'reference'],
            response: 'Nous avons réalisé de nombreux projets au Mali et dans la sous-région. Vous pouvez consulter nos réalisations récentes sur la page <a href="/projects" target="_blank">Nos Projets</a>.'
        },
        {
            id: 'equipe',
            keywords: ['equipe', 'équipe', 'membre', 'collaborateur', 'employé', 'direction', 'fondateur', 'dirigeant'],
            response: 'Notre équipe regroupe des experts passionnés dans chaque secteur. Retrouvez leurs profils sur la page <a href="/team" target="_blank">Notre Équipe</a>.'
        },
        {
            id: 'recrutement',
            keywords: ['carriere', 'carrière', 'emploi', 'recrutement', 'poste', 'job', 'stage', 'travailler', 'embauche', 'candidature', 'cv'],
            response: 'Nous sommes toujours à la recherche de nouveaux talents ! Consultez nos offres disponibles ou déposez votre candidature sur la page <a href="/careers" target="_blank">Carrières</a>.'
        },
        {
            id: 'faq',
            keywords: ['faq', 'question', 'aide', 'support', 'info', 'information', 'aidez'],
            response: 'Vous pouvez consulter les réponses aux questions fréquemment posées sur notre <a href="/faq" target="_blank">Page FAQ</a>.'
        },
        {
            id: 'remerciement',
            keywords: ['merci', 'ok', 'super', 'parfait', 'genial', 'génial', 'merci beaucoup', 'top', 'daccord', 'd\'accord'],
            response: 'C\'est un plaisir d\'échanger avec vous ! N\'hésitez pas si vous avez d\'autres questions. 😊'
        }
    ];

    // Suggestions par défaut
    const defaultSuggestions = [
        'Quels sont vos services ?',
        'Comment obtenir un devis ?',
        'Où êtes-vous situés ?',
        'Proposez-vous des emplois ?'
    ];

    // ============================================
    // MOTEUR D'INTELLIGENCE DU CHATBOT
    // ============================================

    // Normalisation de texte (enlève majuscules, ponctuation, accents)
    function normalizeText(text) {
        return text
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .replace(/[^a-z0-9\s]/g, ' ')
            .trim();
    }

    // Calcul de score de pertinence entre la question et les intentions
    function getBotResponse(input) {
        const cleanInput = normalizeText(input);
        if (!cleanInput) return 'Veuillez saisir une question.';

        const words = cleanInput.split(/\s+/);
        let bestMatch = null;
        let highestScore = 0;

        intentions.forEach(intent => {
            let score = 0;

            intent.keywords.forEach(keyword => {
                const cleanKeyword = normalizeText(keyword);

                // Correspondance exacte de mot-clé
                if (cleanInput.includes(cleanKeyword)) {
                    score += cleanKeyword.length > 4 ? 3 : 2;
                }

                // Correspondance mot par mot
                words.forEach(word => {
                    if (word === cleanKeyword) score += 4;
                    else if (word.length > 3 && cleanKeyword.includes(word)) score += 1.5;
                });
            });

            if (score > highestScore) {
                highestScore = score;
                bestMatch = intent;
            }
        });

        // Seuil minimum de pertinence
        if (bestMatch && highestScore >= 2) {
            return bestMatch.response;
        }

        // Réponse par défaut en cas d'incompréhension
        return 'Je n\'ai pas bien compris votre question. Pouvez-vous reformuler ?<br>' +
               'Vous pouvez aussi nous contacter via notre <a href="/contact" target="_blank">Formulaire de contact</a> ou consulter notre <a href="/faq" target="_blank">FAQ</a>.';
    }

    // ============================================
    // GESTION DU DOM ET DU CHAT
    // ============================================

    function addMessage(text, sender, isHtml = false) {
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        if (isHtml) {
            div.innerHTML = text;
        } else {
            div.textContent = text;
        }
        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function addSuggestions(suggestions) {
        const oldSuggestions = messagesEl.querySelectorAll('.suggestion-container');
        oldSuggestions.forEach(el => el.remove());

        if (!suggestions || suggestions.length === 0) return;

        const container = document.createElement('div');
        container.className = 'suggestion-container';
        suggestions.forEach(s => {
            const btn = document.createElement('button');
            btn.className = 'suggestion-btn';
            btn.textContent = s;
            btn.addEventListener('click', function() {
                addMessage(s, 'user');
                processUserQuery(s);
                container.querySelectorAll('button').forEach(b => b.disabled = true);
            });
            container.appendChild(btn);
        });
        messagesEl.appendChild(container);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function processUserQuery(text) {
        setTimeout(() => {
            const reply = getBotResponse(text);
            addMessage(reply, 'bot', true);
            setTimeout(() => {
                addSuggestions(defaultSuggestions);
            }, 400);
        }, 300 + Math.random() * 300);
    }

    function handleSendMessage() {
        const text = inputEl.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        inputEl.value = '';
        processUserQuery(text);
    }

    // Exportation globale pour compatibilité HTML
    window.sendChatMessage = handleSendMessage;
    window.toggleChatbot = function() {
        const isOpen = windowEl.style.display !== 'none';
        windowEl.style.display = isOpen ? 'none' : 'flex';
        if (!isOpen) {
            inputEl.focus();
            if (!messagesEl.querySelector('.suggestion-container')) {
                addSuggestions(defaultSuggestions);
            }
        }
    };

    // Événements
    if (toggleBtn) toggleBtn.addEventListener('click', window.toggleChatbot);
    if (closeBtn) closeBtn.addEventListener('click', window.toggleChatbot);
    if (sendBtn) sendBtn.addEventListener('click', handleSendMessage);
    if (inputEl) {
        inputEl.addEventListener('keydown', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleSendMessage();
            }
        });
    }
});