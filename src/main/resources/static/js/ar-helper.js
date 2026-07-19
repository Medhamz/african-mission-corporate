// ============================================
// AR HELPER - Réalité Augmentée (version Bootstrap native)
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const arModal = document.getElementById('arModal');
    const arViewer = document.getElementById('arViewer');
    const arLabel = document.getElementById('arModalLabel');

    if (!arModal || !arViewer || !arLabel) {
        console.warn('⚠️ AR : éléments manquants (modal, viewer ou label)');
        return;
    }

    // Écouter l'événement d'ouverture du modal
    arModal.addEventListener('show.bs.modal', function(event) {
        // Récupérer le bouton qui a déclenché l'ouverture
        const button = event.relatedTarget;
        if (!button) {
            console.warn('⚠️ Aucun bouton déclencheur trouvé');
            return;
        }

        // Récupérer les données du bouton
        const modelUrl = button.dataset.model || '/models/activity.glb';
        const title = button.dataset.title || 'Visualisation 3D';

        // Mettre à jour le viewer et le titre
        arViewer.setAttribute('src', modelUrl);
        arLabel.textContent = title;

        console.log(`📦 Modèle chargé : ${modelUrl}`);
    });

    // (Optionnel) Réinitialisation à la fermeture
    arModal.addEventListener('hidden.bs.modal', function () {
        arViewer.setAttribute('camera-controls', '');
    });

    console.log('✅ AR Helper initialisé (mode Bootstrap)');
});