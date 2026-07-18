// ============================================
// CHATBOT WIDGET - VERSION AMÉLIORÉE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('chatbotToggle');
    const closeBtn = document.getElementById('chatbotClose');
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const sendBtn = document.getElementById('chatbotSend');
    const messagesEl = document.getElementById('chatbotMessages');

    // ============================================
    // CONFIGURATION DES INTENTIONS
    // ============================================
    const intentions = [
        { keywords: ['bonjour', 'salut', 'hello', 'coucou'], response: 'Bonjour ! Comment puis-je vous aider aujourd\'hui ?' },
        { keywords: ['service', 'activité', 'prestation', 'btp', 'agrobusiness', 'import', 'export', 'secteur'],
          response: 'Nous intervenons dans plusieurs domaines : BTP, agrobusiness, import-export, développement durable, et bien plus. Consultez notre page <a href="/activities" target="_blank">Activités</a> pour découvrir tous nos services.' },
        { keywords: ['devis', 'tarif', 'prix', 'coût', 'facture'],
          response: 'Pour obtenir un devis personnalisé, merci de nous contacter via notre <a href="/contact" target="_blank">formulaire de contact</a> ou par téléphone au +223 44 39 12 03.' },
        { keywords: ['contact', 'téléphone', 'email', 'joindre', 'adresse'],
          response: 'Vous pouvez nous joindre par téléphone au <strong>+223 44 39 12 03</strong> ou par email à <strong>contact@africanmission.com</strong>. Nous sommes situés à Bamako, Mali.' },
        { keywords: ['projet', 'réalisation', 'chantier', 'client'],
          response: 'Nous avons mené de nombreux projets au Mali et à l\'international. Découvrez quelques-unes de nos réalisations sur notre page <a href="/projects" target="_blank">Projets</a>.' },
        { keywords: ['équipe', 'membre', 'collaborateur', 'employé'],
          response: 'Notre équipe est composée de professionnels expérimentés dans chaque secteur. Vous pouvez consulter leurs profils sur la page <a href="/team" target="_blank">Équipe</a>.' },
        { keywords: ['carrière', 'emploi', 'recrutement', 'poste', 'job'],
          response: 'Nous recrutons régulièrement. Consultez nos offres d\'emploi sur la page <a href="/careers" target="_blank">Carrières</a> ou envoyez-nous votre candidature via le formulaire de contact.' },
        { keywords: ['faq', 'question', 'aide', 'support'],
          response: 'Vous trouverez les réponses aux questions fréquentes dans notre <a href="/faq" target="_blank">FAQ</a>. Si vous ne trouvez pas votre réponse, n\'hésitez pas à nous contacter directement.' },
        { keywords: ['merci', 'ok', 'super', 'parfait', 'génial'],
          response: 'Avec plaisir ! N\'hésitez pas si vous avez d\'autres questions. 😊' },
        // Ajoutez d’autres intentions ici
    ];

    // Suggestions de questions affichées par défaut
    const defaultSuggestions = [
        'Quels sont vos services ?',
        'Comment obtenir un devis ?',
        'Où êtes-vous situés ?',
        'Proposez-vous des emplois ?'
    ];

    // ============================================
    // FONCTIONS PRINCIPALES
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
        // Supprimer les anciennes suggestions
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
                // Simuler l'envoi du message
                addMessage(s, 'user');
                // Répondre
                const reply = getBotResponse(s);
                setTimeout(() => {
                    addMessage(reply, 'bot', true);
                    // Proposer de nouvelles suggestions après la réponse
                    setTimeout(() => {
                        addSuggestions(defaultSuggestions);
                    }, 500);
                }, 300);
                // Désactiver les boutons pour éviter les doubles clics
                container.querySelectorAll('button').forEach(b => b.disabled = true);
            });
            container.appendChild(btn);
        });
        messagesEl.appendChild(container);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function getBotResponse(input) {
        const lower = input.toLowerCase().trim();

        // Vérifier chaque intention
        for (let intent of intentions) {
            for (let keyword of intent.keywords) {
                if (lower.includes(keyword)) {
                    return intent.response;
                }
            }
        }

        // Si aucune intention trouvée
        return 'Je n\'ai pas bien compris votre question. Pouvez-vous reformuler ?<br>Sinon, vous pouvez nous contacter directement via notre <a href="/contact" target="_blank">formulaire</a>.';
    }

    function sendMessage() {
        const text = inputEl.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        inputEl.value = '';

        // Réponse du bot avec délai
        setTimeout(() => {
            const reply = getBotResponse(text);
            addMessage(reply, 'bot', true);
            // Proposer des suggestions après la réponse
            setTimeout(() => {
                addSuggestions(defaultSuggestions);
            }, 500);
        }, 400 + Math.random() * 400);
    }

    // ============================================
    // ÉVÉNEMENTS
    // ============================================

    toggleBtn.addEventListener('click', function() {
        const isOpen = windowEl.style.display !== 'none';
        windowEl.style.display = isOpen ? 'none' : 'flex';
        if (!isOpen) {
            inputEl.focus();
            // Afficher les suggestions lors de l'ouverture
            addSuggestions(defaultSuggestions);
        }
    });

    closeBtn.addEventListener('click', function() {
        windowEl.style.display = 'none';
    });

    sendBtn.addEventListener('click', sendMessage);
    inputEl.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });

    // ============================================
    // INITIALISATION - AFFICHER LES SUGGESTIONS
    // ============================================
    // On les affiche dès que le chat est ouvert (ou au premier message)
    // On les ajoute uniquement quand l'utilisateur ouvre le chat.
    // Pour éviter les doublons, on utilise un flag.
    let suggestionsShown = false;
    toggleBtn.addEventListener('click', function() {
        if (windowEl.style.display !== 'none' && !suggestionsShown) {
            setTimeout(() => {
                addSuggestions(defaultSuggestions);
                suggestionsShown = true;
            }, 300);
        }
    });
});