// ============================================
// AR HELPER - Réalité Augmentée simplifiée
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Gérer les boutons "Voir en AR"
    const arTriggers = document.querySelectorAll('.ar-trigger');
    const arModal = document.getElementById('arModal');
    const arViewer = document.getElementById('arViewer');
    const arLabel = document.getElementById('arModalLabel');

    arTriggers.forEach(btn => {
        btn.addEventListener('click', function(e) {
            const modelUrl = this.dataset.model || '';
            const title = this.dataset.title || 'Visualisation 3D';

            // Mettre à jour le model-viewer
            if (arViewer) {
                // Si un modèle est spécifié, l'utiliser
                if (modelUrl) {
                    arViewer.setAttribute('src', modelUrl);
                } else {
                    // Sinon, afficher un message dans le viewer
                    arViewer.setAttribute('src', '');
                    arViewer.innerHTML = `
                        <div style="display:flex;align-items:center;justify-content:center;height:100%;color:var(--text-secondary);flex-direction:column;gap:16px;">
                            <i class="fas fa-cube" style="font-size:4rem;color:var(--primary-blue);opacity:0.3;"></i>
                            <p style="font-size:1.1rem;max-width:300px;text-align:center;">
                                Ajoutez un modèle 3D (.glb) dans le dossier <br>
                                <code style="background:var(--bg-primary);padding:2px 8px;border-radius:4px;">/static/models/</code>
                            </p>
                            <p style="font-size:0.85rem;color:var(--text-muted);">
                                ou téléchargez un modèle gratuit sur
                                <a href="https://sketchfab.com/search?type=models&features=free&downloadable=true" target="_blank" style="color:var(--primary-blue);">Sketchfab</a>
                            </p>
                        </div>
                    `;
                }
                arLabel.textContent = title || 'Visualisation 3D';

                // Ouvrir le modal
                const modal = new bootstrap.Modal(arModal);
                modal.show();
            }
        });
    });

    // Réinitialiser quand le modal se ferme
    arModal.addEventListener('hidden.bs.modal', function () {
        if (arViewer) {
            // Remettre le viewer dans son état initial
            arViewer.setAttribute('camera-controls', '');
            // Supprimer le contenu additionnel si présent
            const placeholder = arViewer.querySelector('div');
            if (placeholder) {
                placeholder.remove();
            }
        }
    });
});