// ============================================
// THREE.JS - SCROLL ANIMATION 3D
// ============================================

let scene, camera, renderer;
let globe, rings = [];

function initThree() {
    const container = document.getElementById('three-container');
    if (!container) return;

    // Créer la scène
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x0a0a1a);

    // Caméra
    camera = new THREE.PerspectiveCamera(45, container.clientWidth / container.clientHeight, 0.1, 1000);
    camera.position.set(0, 0, 5);

    // Rendu
    renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setSize(container.clientWidth, container.clientHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    container.appendChild(renderer.domElement);

    // Lumières
    const ambientLight = new THREE.AmbientLight(0x404060);
    scene.add(ambientLight);

    const dirLight = new THREE.DirectionalLight(0xffffff, 1);
    dirLight.position.set(1, 1, 1);
    scene.add(dirLight);

    const backLight = new THREE.DirectionalLight(0x3b82f6, 0.5);
    backLight.position.set(-1, -1, -1);
    scene.add(backLight);

    // Créer le globe
    const geometry = new THREE.SphereGeometry(1.2, 64, 64);
    const material = new THREE.MeshPhongMaterial({
        color: 0x3b82f6,
        emissive: 0x1a3a6a,
        emissiveIntensity: 0.2,
        wireframe: false,
        transparent: true,
        opacity: 0.9,
        shininess: 30,
    });
    globe = new THREE.Mesh(geometry, material);
    scene.add(globe);

    // Ajouter des anneaux orbitaux
    for (let i = 0; i < 3; i++) {
        const ringGeo = new THREE.TorusGeometry(1.8 + i * 0.3, 0.02, 16, 100);
        const ringMat = new THREE.MeshBasicMaterial({
            color: 0x3b82f6,
            transparent: true,
            opacity: 0.3 + i * 0.1,
            wireframe: false
        });
        const ring = new THREE.Mesh(ringGeo, ringMat);
        ring.rotation.x = Math.PI / 2;
        ring.rotation.z = i * 0.8;
        scene.add(ring);
        rings.push(ring);
    }

    // Ajouter des particules
    const particlesGeo = new THREE.BufferGeometry();
    const particlesCount = 400;
    const posArray = new Float32Array(particlesCount * 3);
    for (let i = 0; i < particlesCount * 3; i += 3) {
        const r = 2.5 + Math.random() * 2;
        const theta = Math.random() * Math.PI * 2;
        const phi = Math.acos((Math.random() * 2) - 1);
        posArray[i] = r * Math.sin(phi) * Math.cos(theta);
        posArray[i+1] = r * Math.sin(phi) * Math.sin(theta);
        posArray[i+2] = r * Math.cos(phi);
    }
    particlesGeo.setAttribute('position', new THREE.BufferAttribute(posArray, 3));
    const particlesMat = new THREE.PointsMaterial({
        color: 0x3b82f6,
        size: 0.04,
        transparent: true,
        opacity: 0.6
    });
    const particles = new THREE.Points(particlesGeo, particlesMat);
    scene.add(particles);

    // Gérer le redimensionnement
    window.addEventListener('resize', onWindowResize);

    // Démarrer l'animation
    animate();
}

function animate() {
    requestAnimationFrame(animate);

    // Rotation du globe
    if (globe) {
        globe.rotation.y += 0.002;
        globe.rotation.x += 0.001;
    }

    // Rotation des anneaux
    rings.forEach((ring, index) => {
        ring.rotation.y += 0.001 * (index + 1);
        ring.rotation.x += 0.0005 * (index + 1);
    });

    renderer.render(scene, camera);
}

function onWindowResize() {
    const container = document.getElementById('three-container');
    if (!container) return;
    camera.aspect = container.clientWidth / container.clientHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(container.clientWidth, container.clientHeight);
}

// Initialiser au chargement
document.addEventListener('DOMContentLoaded', initThree);

// Effet de parallaxe au scroll
document.addEventListener('scroll', function() {
    const container = document.getElementById('three-container');
    if (!container) return;
    const rect = container.getBoundingClientRect();
    const scrollY = window.scrollY;
    const offset = rect.top + rect.height / 2;
    const factor = (scrollY - offset) / window.innerHeight;
    if (camera) {
        camera.position.y = factor * 0.5;
        camera.lookAt(0, 0, 0);
    }
});