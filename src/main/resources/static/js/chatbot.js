// ============================================
// CHATBOT WIDGET - LOGIQUE
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const toggleBtn = document.getElementById('chatbotToggle');
    const closeBtn = document.getElementById('chatbotClose');
    const windowEl = document.getElementById('chatbotWindow');
    const inputEl = document.getElementById('chatbotInput');
    const sendBtn = document.getElementById('chatbotSend');
    const messagesEl = document.getElementById('chatbotMessages');

    // Ouvrir / fermer
    toggleBtn.addEventListener('click', function() {
        const isOpen = windowEl.style.display !== 'none';
        windowEl.style.display = isOpen ? 'none' : 'flex';
        if (!isOpen) {
            inputEl.focus();
        }
    });

    closeBtn.addEventListener('click', function() {
        windowEl.style.display = 'none';
    });

    // Envoyer un message
    function sendMessage() {
        const text = inputEl.value.trim();
        if (!text) return;

        // Ajouter le message de l'utilisateur
        addMessage(text, 'user');
        inputEl.value = '';

        // Simuler une réponse du bot (après un délai)
        setTimeout(() => {
            const response = getBotResponse(text);
            addMessage(response, 'bot');
        }, 500 + Math.random() * 400);
    }

    function addMessage(text, sender) {
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        div.textContent = text;
        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function getBotResponse(input) {
        const lower = input.toLowerCase();
        if (lower.includes('bonjour') || lower.includes('salut')) {
            return 'Bonjour ! Comment puis-je vous aider aujourd\'hui ?';
        }
        if (lower.includes('service') || lower.includes('activité')) {
            return 'Nous proposons des services dans le BTP, l\'agrobusiness, l\'import-export, et bien plus. Consultez notre page "Activités" pour plus de détails.';
        }
        if (lower.includes('contact') || lower.includes('téléphone') || lower.includes('email')) {
            return 'Vous pouvez nous joindre par téléphone au +223 44 39 12 03 ou par email à contact@africanmission.com.';
        }
        if (lower.includes('projet') || lower.includes('réalisatio')) {
            return 'Nous avons mené de nombreux projets au Mali et à l\'international. Rendez-vous dans la section "Projets" pour découvrir nos réalisations.';
        }
        if (lower.includes('équipe') || lower.includes('membre')) {
            return 'Notre équipe est composée de professionnels expérimentés. Consultez la page "Équipe" pour en savoir plus.';
        }
        if (lower.includes('carrière') || lower.includes('emploi') || lower.includes('recrutement')) {
            return 'Nous sommes toujours à la recherche de talents. Rendez-vous sur la page "Carrières" pour voir nos offres.';
        }
        if (lower.includes('merci')) {
            return 'Avec plaisir ! N\'hésitez pas si vous avez d\'autres questions.';
        }
        return 'Je vous remercie pour votre question. Pour plus d\'informations, je vous invite à parcourir notre site ou à nous contacter directement.';
    }

    // Événements
    sendBtn.addEventListener('click', sendMessage);
    inputEl.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });

    // Si le clic est en dehors du widget, on peut le fermer (optionnel)
    // Ici on ne le fait pas pour ne pas gêner l'utilisateur.
});