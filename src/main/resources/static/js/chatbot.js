// ============================================
// CHATBOT WIDGET - VERSION INTELLIGENTE AVANCÉE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('chatbotToggle');
    const closeBtn = document.getElementById('chatbotClose');
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const sendBtn = document.getElementById('chatbotSend');
    const messagesEl = document.getElementById('chatbotMessages');

    // ============================================
    // BASE DE CONNAISSANCES & INTENTIONS
    // ============================================
    const intentions = [
        {
            id: 'salutation',
            keywords: ['bonjour', 'salut', 'hello', 'coucou', 'bonsoir', 'hey', 'bienvenue', 'cc'],
            response: function() {
                const hour = new Date().getHours();
                const greeting = (hour >= 18 || hour < 5) ? 'Bonsoir' : 'Bonjour';
                return `${greeting} ! Ravi de vous accueillir sur African Mission Corporate. Comment puis-je vous guider ?`;
            }
        },
        {
            id: 'services_generaux',
            keywords: ['service', 'activité', 'activite', 'prestation', 'secteur', 'faires', 'metier', 'domaine', 'proposer', 'offre', 'btp', 'immobilier', 'agrobusiness', 'import', 'export'],
            response: 'African Mission Corporate est une entreprise polyvalente de référence au Mali :<br><br>' +
                      '• 🏢 <strong>Promotion Immobilière & BTP</strong> : Construction durable et projets urbains.<br>' +
                      '• 🌾 <strong>Agrobusiness</strong> : Transformation et valorisation agricole.<br>' +
                      '• 📦 <strong>Import-Export</strong> : Logistique et négoce international.<br>' +
                      '• ♻️ <strong>Développement Durable</strong> : Solutions écologiques et énergie.<br><br>' +
                      'Découvrez nos offres détaillées sur la page <a href="/activities">Nos Activités</a>.'
        },
        {
            id: 'devis_tarif',
            keywords: ['devis', 'tarif', 'prix', 'cout', 'coût', 'facture', 'estimation', 'payer', 'combien', 'budget', 'tarififcation'],
            response: 'Chaque projet étant unique, nous établissons des devis sur mesure.<br><br>' +
                      '📌 Vous pouvez solliciter un devis directement via notre <a href="/contact">Formulaire de contact</a> ou contacter notre service commercial au <strong>+223 44 39 12 03</strong>.'
        },
        {
            id: 'contact_info',
            keywords: ['contact', 'telephone', 'téléphone', 'email', 'mail', 'joindre', 'adresse', 'emplacement', 'situé', 'situer', 'ou', 'siège', 'bureau', 'bamako', 'mali', 'appel'],
            response: '📍 <strong>Siège social :</strong> Bamako, Mali<br>' +
                      '📞 <strong>Téléphone :</strong> +223 44 39 12 03<br>' +
                      '✉️ <strong>Email :</strong> contact@africanmission.com<br>' +
                      '🕒 <strong>Horaires :</strong> Lun - Ven : 08h00 - 17h00'
        },
        {
            id: 'horaires',
            keywords: ['heure', 'horaire', 'ouverture', 'fermeture', 'quand', 'ouvert', 'disponibilité', 'jour', 'ferme', 'ouvre'],
            response: '🕒 Nos locaux et nos services administratifs sont ouverts du <strong>Lundi au Vendredi de 08h00 à 17h00</strong>.'
        },
        {
            id: 'projets',
            keywords: ['projet', 'realisation', 'réalisation', 'chantier', 'client', 'travaux', 'portfolio', 'reference', 'accomplissement'],
            response: 'Nous menons d\'envergure des projets structurants à Bamako et à l\'international.<br>' +
                      'Consultez nos récentes réalisations sur notre page dédiée : <a href="/projects">Nos Projets</a>.'
        },
        {
            id: 'equipe',
            keywords: ['equipe', 'équipe', 'membre', 'collaborateur', 'employé', 'direction', 'fondateur', 'dirigeant', 'manager', 'ceo'],
            response: 'African Mission Corporate s\'appuie sur des ingénieurs et managers chevronnés.<br>' +
                      'Apprenez-en plus sur notre gouvernance via <a href="/team">Notre Équipe</a>.'
        },
        {
            id: 'recrutement',
            keywords: ['carriere', 'carrière', 'emploi', 'recrutement', 'poste', 'job', 'stage', 'travailler', 'embauche', 'candidature', 'cv', 'lettre'],
            response: 'Nous recrutons régulièrement des talents passionnés.<br>' +
                      'Retrouvez nos opportunités et déposez votre candidature spontanée sur <a href="/careers">Carrières</a>.'
        },
        {
            id: 'faq',
            keywords: ['faq', 'question', 'aide', 'support', 'info', 'information', 'aidez', 'comment'],
            response: 'Pour répondre rapidement à vos questions fréquentes, visitez notre <a href="/faq">Centre d\'aide / FAQ</a>.'
        },
        {
            id: 'remerciement',
            keywords: ['merci', 'ok', 'super', 'parfait', 'genial', 'génial', 'merci beaucoup', 'top', 'daccord', 'd\'accord', 'bravo'],
            response: 'Je vous en prie ! C\'est un plaisir de vous assister. Avez-vous besoin d\'autre chose ? 😊'
        }
    ];

    const defaultSuggestions = [
        'Quels sont vos services ?',
        'Comment obtenir un devis ?',
        'Où vous trouvez-vous ?',
        'Consulter les offres d\'emploi'
    ];

    // ============================================
    // MOTEUR D'INTELLIGENCE & ALGORITHMES
    // ============================================

    function normalizeText(text) {
        return text
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .replace(/[^a-z0-9\s]/g, ' ')
            .trim();
    }

    // Algorithme de Levenshtein pour la tolérance aux fautes de frappe
    function levenshteinDistance(a, b) {
        if (a.length === 0) return b.length;
        if (b.length === 0) return a.length;
        const matrix = [];
        for (let i = 0; i <= b.length; i++) matrix[i] = [i];
        for (let j = 0; j <= a.length; j++) matrix[0][j] = j;

        for (let i = 1; i <= b.length; i++) {
            for (let j = 1; j <= a.length; j++) {
                if (b.charAt(i - 1) === a.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1];
                } else {
                    matrix[i][j] = Math.min(
                        matrix[i - 1][j - 1] + 1,
                        matrix[i][j - 1] + 1,
                        matrix[i - 1][j] + 1
                    );
                }
            }
        }
        return matrix[i][j];
    }

    function getBotResponse(input) {
        const cleanInput = normalizeText(input);
        if (!cleanInput) return 'Veuillez saisir une question ou un terme de recherche.';

        const words = cleanInput.split(/\s+/);
        let bestMatch = null;
        let highestScore = 0;

        intentions.forEach(intent => {
            let score = 0;

            intent.keywords.forEach(keyword => {
                const cleanKeyword = normalizeText(keyword);

                if (cleanInput.includes(cleanKeyword)) {
                    score += cleanKeyword.length > 4 ? 4 : 2.5;
                }

                words.forEach(word => {
                    if (word === cleanKeyword) {
                        score += 5;
                    } else if (word.length > 3 && cleanKeyword.length > 3) {
                        const dist = levenshteinDistance(word, cleanKeyword);
                        if (dist <= 1) score += 3;
                        else if (dist <= 2) score += 1.5;
                    }
                });
            });

            if (score > highestScore) {
                highestScore = score;
                bestMatch = intent;
            }
        });

        if (bestMatch && highestScore >= 2) {
            return typeof bestMatch.response === 'function' ? bestMatch.response() : bestMatch.response;
        }

        return 'Je ne suis pas sûr de bien comprendre votre demande.<br><br>' +
               'Vous pouvez reformuler votre question, faire une recherche via notre barre de recherche, ou consulter notre <a href="/contact">Formulaire de contact</a>.';
    }

    // ============================================
    // GESTION ET INTERACTION DOM
    // ============================================

    function addMessage(text, sender, isHtml = false) {
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        if (isHtml) div.innerHTML = text;
        else div.textContent = text;

        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
        saveHistory();
    }

    function showTypingIndicator() {
        const indicator = document.createElement('div');
        indicator.className = 'message bot typing-indicator';
        indicator.id = 'typingIndicator';
        indicator.innerHTML = '<span class="dot"></span><span class="dot"></span><span class="dot"></span>';
        messagesEl.appendChild(indicator);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function removeTypingIndicator() {
        const indicator = document.getElementById('typingIndicator');
        if (indicator) indicator.remove();
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
        showTypingIndicator();

        setTimeout(() => {
            removeTypingIndicator();
            const reply = getBotResponse(text);
            addMessage(reply, 'bot', true);

            setTimeout(() => {
                addSuggestions(defaultSuggestions);
            }, 300);
        }, 500 + Math.random() * 400);
    }

    function handleSendMessage() {
        const text = inputEl.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        inputEl.value = '';
        processUserQuery(text);
    }

    function saveHistory() {
        if (messagesEl) {
            sessionStorage.setItem('amc_chat_history', messagesEl.innerHTML);
        }
    }

    function loadHistory() {
        const history = sessionStorage.getItem('amc_chat_history');
        if (history && messagesEl) {
            messagesEl.innerHTML = history;
            messagesEl.scrollTop = messagesEl.scrollHeight;
        } else {
            addSuggestions(defaultSuggestions);
        }
    }

    window.toggleChatbot = function() {
        const isOpen = windowEl.style.display !== 'none';
        windowEl.style.display = isOpen ? 'none' : 'flex';
        if (!isOpen) {
            inputEl.focus();
            if (messagesEl.children.length <= 1) {
                loadHistory();
            }
        }
    };

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