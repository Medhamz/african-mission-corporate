// ============================================
// CHATBOT WIDGET - VERSION ENRICHIE & INTELLIGENTE
// ============================================

window.toggleChatbot = function() {
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const messagesEl = document.getElementById('chatbotMessages');

    if (!windowEl) return;

    const isHidden = windowEl.style.display === 'none' || windowEl.style.display === '';
    windowEl.style.display = isHidden ? 'flex' : 'none';

    if (isHidden && inputEl) {
        inputEl.focus();
        if (messagesEl && messagesEl.children.length <= 1) {
            loadHistory();
        }
    }
};

document.addEventListener('DOMContentLoaded', function() {
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const sendBtn = document.getElementById('chatbotSend');
    const messagesEl = document.getElementById('chatbotMessages');
    const closeBtn = document.getElementById('chatbotClose');

    // BASE DE CONNAISSANCES COMPLÈTE DU SITE
    const intentions = [
        {
            id: 'salutation',
            keywords: ['bonjour', 'salut', 'hello', 'coucou', 'bonsoir', 'hey', 'cc'],
            response: function() {
                const hour = new Date().getHours();
                const greeting = (hour >= 18 || hour < 5) ? 'Bonsoir' : 'Bonjour';
                return `${greeting} ! Bienvenue chez African Mission Corporate. Comment puis-je vous aider aujourd'hui ?`;
            }
        },
        {
            id: 'zoumana_equipe',
            keywords: ['zoumana', 'qui est zoumana', 'zoumana traore', 'fondateur', 'pdg', 'ceo', 'directeur', 'patron', 'chef'],
            response: '<strong>Zoumana</strong> fait partie de l\'équipe dirigeante et des figures clés d\'African Mission Corporate.<br><br>' +
                      'Pour découvrir son parcours détaillé et l\'ensemble des membres de notre direction, visitez la page <a href="/team">Notre Équipe</a>.'
        },
        {
            id: 'pays_localisation',
            keywords: ['pays', 'quel pays', 'situe', 'situer', 'ou se trouve', 'emplacement', 'ville', 'adresse', 'bamako', 'mali', 'siège', 'siege'],
            response: '📍 <strong>African Mission Corporate</strong> est basée au <strong>Mali</strong>, avec son siège social situé à <strong>Bamako</strong>.<br><br>' +
                      'Nous opérons au Mali et sur l\'ensemble du continent africain. Consulter notre page <a href="/contact">Contact</a> pour nos coordonnées exactes.'
        },
        {
            id: 'services_generaux',
            keywords: ['service', 'activité', 'activite', 'prestation', 'secteur', 'faires', 'metier', 'domaine', 'proposer', 'offre', 'btp', 'immobilier', 'agrobusiness', 'import', 'export'],
            response: 'African Mission Corporate propose des services stratégiques au Mali :<br><br>' +
                      '• 🏢 <strong>Promotion Immobilière & BTP</strong> : Construction et aménagement.<br>' +
                      '• 🌾 <strong>Agrobusiness</strong> : Production et transformation agricole.<br>' +
                      '• 📦 <strong>Import-Export</strong> : Commerce international et logistique.<br>' +
                      '• ♻️ <strong>Développement Durable</strong> : Solutions énergétiques et écologiques.<br><br>' +
                      'Découvrez toutes nos offres sur la page <a href="/activities">Nos Activités</a>.'
        },
        {
            id: 'devis_tarif',
            keywords: ['devis', 'tarif', 'prix', 'cout', 'coût', 'facture', 'estimation', 'payer', 'combien', 'budget'],
            response: 'Nos devis sont personnalisés selon la nature de votre projet.<br><br>' +
                      '📌 Contactez notre service commercial via le <a href="/contact">Formulaire de contact</a> ou directement au <strong>+223 44 39 12 03</strong>.'
        },
        {
            id: 'contact_info',
            keywords: ['contact', 'telephone', 'téléphone', 'email', 'mail', 'joindre', 'appeler', 'numero', 'numéro'],
            response: '📞 <strong>Téléphone :</strong> +223 44 39 12 03<br>' +
                      '✉️ <strong>Email :</strong> contact@africanmission.com<br>' +
                      '📍 <strong>Adresse :</strong> Bamako, Mali<br>' +
                      '🕒 <strong>Horaires :</strong> Lundi - Vendredi : 08h00 - 17h00'
        },
        {
            id: 'equipe_generale',
            keywords: ['equipe', 'équipe', 'membre', 'collaborateur', 'employé', 'direction', 'qui travaille'],
            response: 'Notre équipe réunit des experts et des ingénieurs qualifiés au service du développement de l\'Afrique.<br>' +
                      'Retrouvez les profils de nos dirigeants sur la page <a href="/team">Notre Équipe</a>.'
        },
        {
            id: 'recrutement',
            keywords: ['carriere', 'carrière', 'emploi', 'recrutement', 'poste', 'job', 'stage', 'travailler', 'embauche', 'cv'],
            response: 'Vous souhaitez rejoindre African Mission Corporate ?<br>' +
                      'Consultez nos offres et déposez votre candidature sur notre page <a href="/careers">Carrières</a>.'
        },
        {
            id: 'projets',
            keywords: ['projet', 'realisation', 'réalisation', 'chantier', 'travaux', 'portfolio', 'reference'],
            response: 'Découvrez nos projets structurants au Mali et en Afrique subsaharienne sur la page <a href="/projects">Nos Projets</a>.'
        },
        {
            id: 'remerciement',
            keywords: ['merci', 'ok', 'super', 'parfait', 'top', 'daccord', 'd\'accord'],
            response: 'Je vous en prie ! C\'est un plaisir de vous renseigner. Avez-vous d\'autres questions ? 😊'
        }
    ];

    const defaultSuggestions = [
        'Qui est Zoumana ?',
        'Dans quel pays êtes-vous ?',
        'Quels sont vos services ?',
        'Comment demander un devis ?'
    ];

    function normalizeText(text) {
        return text
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .replace(/[^a-z0-9\s]/g, ' ')
            .trim();
    }

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
        return matrix[b.length][a.length];
    }

    function getBotResponse(input) {
        const cleanInput = normalizeText(input);
        if (!cleanInput) return 'Veuillez poser une question.';

        const words = cleanInput.split(/\s+/);
        let bestMatch = null;
        let highestScore = 0;

        intentions.forEach(intent => {
            let score = 0;

            intent.keywords.forEach(keyword => {
                const cleanKeyword = normalizeText(keyword);

                // Correspondance exacte ou partielle de phrase
                if (cleanInput.includes(cleanKeyword)) {
                    score += cleanKeyword.length > 5 ? 6 : 4;
                }

                // Correspondance mot par mot avec tolérance aux fautes
                words.forEach(word => {
                    if (word === cleanKeyword) {
                        score += 5;
                    } else if (word.length > 3 && cleanKeyword.length > 3) {
                        const dist = levenshteinDistance(word, cleanKeyword);
                        if (dist <= 1) score += 3;
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

        return 'Je n\'ai pas la réponse exacte à cette question dans ma base de données.<br><br>' +
               'Vous pouvez contacter notre équipe via la page <a href="/contact">Contact</a> ou nous appeler au <strong>+223 44 39 12 03</strong>.';
    }

    function addMessage(text, sender, isHtml = false) {
        if (!messagesEl) return;
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        if (isHtml) div.innerHTML = text;
        else div.textContent = text;

        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
        saveHistory();
    }

    function showTypingIndicator() {
        if (!messagesEl) return;
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
        if (!messagesEl) return;
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
        }, 400 + Math.random() * 300);
    }

    function handleSendMessage() {
        if (!inputEl) return;
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