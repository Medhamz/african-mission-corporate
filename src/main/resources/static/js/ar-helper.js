// ============================================
// AR HELPER - Réalité Augmentée simplifiée
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Gérer les boutons "Voir en AR"
    const arTriggers = document.querySelectorAll('.ar-trigger');
    const arModal = document.getElementById('arModal');
    const arViewer = document.getElementById('arViewer');
    const arLabel = document.getElementById('arModalLabel');

    // Vérifier que tous les éléments existent
    if (!arModal || !arViewer || !arLabel) {
        console.warn('AR : éléments manquants (modal, viewer ou label)');
        return;
    }

    arTriggers.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault(); // Empêche tout comportement par défaut

            const modelUrl = this.dataset.model || '/models/activity.glb';
            const title = this.dataset.title || 'Visualisation 3D';

            // Mettre à jour le model-viewer (ne pas toucher à innerHTML)
            arViewer.setAttribute('src', modelUrl);
            arLabel.textContent = title;

            // Ouvrir le modal avec Bootstrap
            const modal = new bootstrap.Modal(arModal);
            modal.show();
        });
    });

    // Réinitialisation à la fermeture du modal (optionnel)
    arModal.addEventListener('hidden.bs.modal', function () {
        // Remettre le viewer dans son état initial
        arViewer.setAttribute('camera-controls', '');
        // Le slot poster sera automatiquement réaffiché par model-viewer
    });
});