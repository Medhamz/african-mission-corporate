// ============================================
// AR HELPER - Réalité Augmentée (version clic explicite)
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    const arTriggers = document.querySelectorAll('.ar-trigger');
    const arModal = document.getElementById('arModal');
    const arViewer = document.getElementById('arViewer');
    const arLabel = document.getElementById('arModalLabel');

    if (!arModal || !arViewer || !arLabel) {
        console.warn('⚠️ AR : éléments manquants (modal, viewer ou label)');
        return;
    }

    // Supprimer les attributs data-bs-toggle et data-bs-target pour éviter les conflits
    arTriggers.forEach(btn => {
        btn.removeAttribute('data-bs-toggle');
        btn.removeAttribute('data-bs-target');
    });

    // Attacher un écouteur de clic explicite
    arTriggers.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            const modelUrl = this.dataset.model || '/models/activity.glb';
            const title = this.dataset.title || 'Visualisation 3D';
            // Mettre à jour le viewer
            arViewer.setAttribute('src', modelUrl);
            arLabel.textContent = title;
            // Ouvrir le modal avec Bootstrap
            const modal = new bootstrap.Modal(arModal);
            modal.show();
            console.log('✅ Modal AR ouvert');
        });
    });

    console.log('✅ AR Helper initialisé (mode clic explicite)');
});