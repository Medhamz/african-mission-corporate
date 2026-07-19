// ============================================
// RÉALITÉ AUGMENTÉE (AR) - VUE 3D
// ============================================

let arScene, arCamera, arRenderer;
let arModel;

function initAR() {
    const container = document.getElementById('ar-container');
    if (!container) return;

    // Vérifier le support WebXR
    if (!navigator.xr) {
        container.innerHTML = '<p style="color: var(--text-secondary); text-align: center;">Votre navigateur ne supporte pas la réalité augmentée. Utilisez un appareil compatible (Android/Chrome ou iOS/Safari).</p>';
        return;
    }

    // Créer la scène
    arScene = new THREE.Scene();
    arScene.background = new THREE.Color(0x111128);

    // Caméra
    arCamera = new THREE.PerspectiveCamera(45, container.clientWidth / container.clientHeight, 0.1, 1000);
    arCamera.position.set(0, 0, 5);

    // Rendu
    arRenderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    arRenderer.setSize(container.clientWidth, container.clientHeight);
    arRenderer.setPixelRatio(window.devicePixelRatio);
    container.appendChild(arRenderer.domElement);

    // Lumières
    const ambientLight = new THREE.AmbientLight(0x404060);
    arScene.add(ambientLight);

    const dirLight = new THREE.DirectionalLight(0xffffff, 1);
    dirLight.position.set(1, 1, 1);
    arScene.add(dirLight);

    // Charger un modèle 3D (GLTF/GLB)
    const loader = new THREE.GLTFLoader();
    const modelUrl = '/models/ar-model.glb';  // Assurez-vous que le fichier existe
    loader.load(modelUrl, function(gltf) {
        arModel = gltf.scene;
        arModel.scale.set(1.2, 1.2, 1.2);
        arScene.add(arModel);
    }, undefined, function(error) {
        console.error('Erreur chargement modèle AR', error);
        // Modèle de secours : un cube coloré
        const geometry = new THREE.BoxGeometry(1.5, 1.5, 1.5);
        const material = new THREE.MeshPhongMaterial({ color: 0x3b82f6 });
        const cube = new THREE.Mesh(geometry, material);
        arScene.add(cube);
    });

    // Ajouter une grille de référence
    const gridHelper = new THREE.GridHelper(4, 10, 0x3b82f6, 0x1a3a6a);
    gridHelper.position.y = -1.2;
    arScene.add(gridHelper);

    // Bouton pour activer l'AR (WebXR)
    const arButton = document.getElementById('ar-activate-btn');
    if (arButton) {
        arButton.addEventListener('click', function() {
            activateAR();
        });
    }

    // Animation
    arAnimate();

    // Redimensionnement
    window.addEventListener('resize', function() {
        const container = document.getElementById('ar-container');
        if (!container) return;
        arCamera.aspect = container.clientWidth / container.clientHeight;
        arCamera.updateProjectionMatrix();
        arRenderer.setSize(container.clientWidth, container.clientHeight);
    });
}

function arAnimate() {
    requestAnimationFrame(arAnimate);
    if (arModel) {
        arModel.rotation.y += 0.005;
        arModel.rotation.x = Math.sin(Date.now() * 0.001) * 0.1;
    }
    arRenderer.render(arScene, arCamera);
}

function activateAR() {
    // Vérifier le support WebXR
    if (!navigator.xr) {
        alert('Votre navigateur ne supporte pas la réalité augmentée. Utilisez un appareil compatible.');
        return;
    }

    // Si supporté, proposer de lancer la session AR
    if (confirm('Lancer la visualisation en réalité augmentée ? (Le modèle apparaîtra dans votre environnement réel)')) {
        // Ici, on pourrait intégrer un véritable mode AR via WebXR
        // Pour l'instant, on simule un effet de "plein écran" avec des instructions.
        alert('Fonctionnalité AR en développement. Sur un appareil compatible, vous pourrez visualiser le modèle dans votre environnement via la caméra.');
        // Pour une démonstration, on peut simplement agrandir le conteneur
        const container = document.getElementById('ar-container');
        container.style.position = 'fixed';
        container.style.top = '0';
        container.style.left = '0';
        container.style.width = '100vw';
        container.style.height = '100vh';
        container.style.zIndex = '9999';
        container.style.background = '#0a0a1a';
        // Ajouter un bouton de fermeture
        const closeBtn = document.createElement('button');
        closeBtn.textContent = '✕ Fermer';
        closeBtn.style.cssText = 'position: absolute; top: 20px; right: 20px; background: rgba(255,255,255,0.2); border: none; color: #fff; padding: 10px 20px; border-radius: 8px; cursor: pointer; font-size: 1rem;';
        closeBtn.onclick = function() {
            container.style.position = 'relative';
            container.style.width = '';
            container.style.height = '';
            container.style.zIndex = '';
            container.style.background = '';
            this.remove();
        };
        container.appendChild(closeBtn);
    }
}

// Initialiser au chargement
document.addEventListener('DOMContentLoaded', initAR);