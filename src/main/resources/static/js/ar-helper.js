// ============================================
// AR HELPER - Réalité Augmentée simplifiée
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 ar-helper.js chargé');

    // Récupérer les éléments
    const arTriggers = document.querySelectorAll('.ar-trigger');
    const arModal = document.getElementById('arModal');
    const arViewer = document.getElementById('arViewer');
    const arLabel = document.getElementById('arModalLabel');

    console.log(`🔍 Boutons AR trouvés : ${arTriggers.length}`);
    console.log('Modal trouvé :', arModal ? '✅' : '❌');
    console.log('Viewer trouvé :', arViewer ? '✅' : '❌');
    console.log('Label trouvé :', arLabel ? '✅' : '❌');

    if (!arModal || !arViewer || !arLabel) {
        console.warn('⚠️ AR : éléments manquants, abandon.');
        return;
    }

    if (arTriggers.length === 0) {
        console.warn('⚠️ Aucun bouton .ar-trigger trouvé.');
        return;
    }

    // Attacher les événements
    arTriggers.forEach((btn, index) => {
        console.log(`📌 Attachement événement au bouton ${index + 1}`);
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('🖱️ Clic sur le bouton AR');

            const modelUrl = this.dataset.model || '/models/activity.glb';
            const title = this.dataset.title || 'Visualisation 3D';

            // Mettre à jour le viewer
            arViewer.setAttribute('src', modelUrl);
            arLabel.textContent = title;
            console.log(`📦 Modèle chargé : ${modelUrl}`);

            // Ouvrir le modal
            try {
                const modal = new bootstrap.Modal(arModal);
                modal.show();
                console.log('✅ Modal ouvert');
            } catch (error) {
                console.error('❌ Erreur lors de l\'ouverture du modal :', error);
            }
        });
    });

    // Réinitialisation à la fermeture
    arModal.addEventListener('hidden.bs.modal', function () {
        console.log('🔒 Modal fermé, réinitialisation');
        arViewer.setAttribute('camera-controls', '');
    });

    console.log('✅ AR Helper initialisé avec succès');
});