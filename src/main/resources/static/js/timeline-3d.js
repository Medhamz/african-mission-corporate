// ============================================
// TIMELINE 3D - GLOBE AVEC MARQUEURS
// ============================================

let timelineScene, timelineCamera, timelineRenderer;
let globeMesh, markersGroup = [];
let isTimelineReady = false;

function initTimeline() {
    const container = document.getElementById('timeline-globe');
    if (!container) return;

    // Vérifier que Three.js est chargé
    if (typeof THREE === 'undefined') {
        container.innerHTML = '<p style="color:var(--text-muted);text-align:center;padding-top:40%;">Chargement du globe 3D...</p>';
        return;
    }

    // --- SCENE ---
    timelineScene = new THREE.Scene();
    timelineScene.background = null; // transparent

    // --- CAMERA ---
    const width = container.clientWidth;
    const height = container.clientHeight;
    timelineCamera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
    timelineCamera.position.set(0, 0.5, 4);

    // --- RENDERER ---
    timelineRenderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    timelineRenderer.setSize(width, height);
    timelineRenderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    container.appendChild(timelineRenderer.domElement);

    // --- LIGHTS ---
    const ambientLight = new THREE.AmbientLight(0x404060, 0.6);
    timelineScene.add(ambientLight);

    const dirLight = new THREE.DirectionalLight(0xffffff, 1.2);
    dirLight.position.set(1, 2, 3);
    timelineScene.add(dirLight);

    const backLight = new THREE.DirectionalLight(0x3b82f6, 0.5);
    backLight.position.set(-1, -0.5, -1);
    timelineScene.add(backLight);

    // --- GLOBE ---
    const geometry = new THREE.SphereGeometry(1, 64, 64);
    const material = new THREE.MeshPhongMaterial({
        color: 0x3b82f6,
        emissive: 0x0a1628,
        emissiveIntensity: 0.15,
        transparent: true,
        opacity: 0.9,
        shininess: 30
    });
    globeMesh = new THREE.Mesh(geometry, material);
    timelineScene.add(globeMesh);

    // WIREFRAME
    const linesMaterial = new THREE.LineBasicMaterial({ color: 0x60a5fa, transparent: true, opacity: 0.2 });
    const linesGeometry = new THREE.SphereGeometry(1.01, 24, 12);
    const wireframe = new THREE.LineSegments(
        new THREE.WireframeGeometry(linesGeometry),
        linesMaterial
    );
    timelineScene.add(wireframe);

    // --- ANNEAUX ---
    const ringMaterial = new THREE.MeshBasicMaterial({
        color: 0x3b82f6,
        transparent: true,
        opacity: 0.1,
        side: THREE.DoubleSide,
        wireframe: true
    });
    for (let i = 0; i < 3; i++) {
        const ringGeo = new THREE.TorusGeometry(1.3 + i * 0.25, 0.02, 16, 64);
        const ring = new THREE.Mesh(ringGeo, ringMaterial);
        ring.rotation.x = Math.PI / 2 + (i * 0.5);
        ring.rotation.z = i * 0.8;
        timelineScene.add(ring);
    }

    // --- PARTICULES ---
    const particlesGeo = new THREE.BufferGeometry();
    const particlesCount = 300;
    const positions = new Float32Array(particlesCount * 3);
    for (let i = 0; i < particlesCount * 3; i += 3) {
        const radius = 2.5 + Math.random() * 2.0;
        const theta = Math.random() * Math.PI * 2;
        const phi = Math.acos((Math.random() * 2) - 1);
        positions[i] = radius * Math.sin(phi) * Math.cos(theta);
        positions[i+1] = radius * Math.sin(phi) * Math.sin(theta);
        positions[i+2] = radius * Math.cos(phi);
    }
    particlesGeo.setAttribute('position', new THREE.BufferAttribute(positions, 3));
    const particlesMat = new THREE.PointsMaterial({
        color: 0x93b4f0,
        size: 0.015,
        transparent: true,
        opacity: 0.5,
        blending: THREE.AdditiveBlending
    });
    const particles = new THREE.Points(particlesGeo, particlesMat);
    timelineScene.add(particles);

    // --- AJOUT DES MARQUEURS (points sur le globe) ---
    addMarkers();

    // --- CONTROLS (OrbitControls) ---
    const controls = new THREE.OrbitControls(timelineCamera, timelineRenderer.domElement);
    controls.enableZoom = true;
    controls.zoomSpeed = 0.8;
    controls.rotateSpeed = 0.6;
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.autoRotate = true;
    controls.autoRotateSpeed = 0.8;
    controls.target.set(0, 0, 0);

    // --- ANIMATION ---
    function animate() {
        requestAnimationFrame(animate);

        // Rotation lente du globe et des particules
        globeMesh.rotation.y += 0.001;
        wireframe.rotation.y = globeMesh.rotation.y;
        particles.rotation.y += 0.0005;

        controls.update();
        timelineRenderer.render(timelineScene, timelineCamera);
    }
    animate();

    // --- REDIMENSIONNEMENT ---
    window.addEventListener('resize', () => {
        const w = container.clientWidth;
        const h = container.clientHeight;
        if (w > 0 && h > 0) {
            timelineRenderer.setSize(w, h);
            timelineCamera.aspect = w / h;
            timelineCamera.updateProjectionMatrix();
        }
    });

    // --- INTERACTION AU SCROLL : accélération / ralentissement ---
    window.addEventListener('scroll', () => {
        const scrollY = window.scrollY;
        const maxScroll = 800;
        const speed = 0.5 + (scrollY / maxScroll) * 1.5;
        controls.autoRotateSpeed = Math.min(speed, 3.0);
    });

    isTimelineReady = true;
    console.log('✅ Timeline 3D initialisée');
}

function addMarkers() {
    // Récupérer les données
    if (typeof timelineData === 'undefined') {
        console.warn('Données timeline manquantes');
        return;
    }

    // Positions sur le globe (réparties aléatoirement mais sur des zones "réalistes")
    // Pour une démonstration, on place les marqueurs sur l'Afrique et l'Europe
    const positions = [
        { lat: 12.65, lng: -8.0 },   // Bamako
        { lat: 34.0, lng: -6.0 },    // Maroc
        { lat: 14.0, lng: -2.0 },    // Burkina Faso
        { lat: 36.0, lng: 2.0 },     // Algérie
        { lat: 28.0, lng: -8.0 },    // Mauritanie
        { lat: 10.0, lng: 4.0 }      // Nigeria
    ];

    // Fonction pour convertir lat/lng en coordonnées 3D
    function latLngToVector(lat, lng, radius) {
        const phi = (90 - lat) * Math.PI / 180;
        const theta = lng * Math.PI / 180;
        return new THREE.Vector3(
            radius * Math.sin(phi) * Math.cos(theta),
            radius * Math.cos(phi),
            radius * Math.sin(phi) * Math.sin(theta)
        );
    }

    const radius = 1.05; // légèrement au-dessus de la surface du globe

    timelineData.forEach((item, index) => {
        const pos = positions[index % positions.length];
        const vector = latLngToVector(pos.lat, pos.lng, radius);

        // Créer un groupe pour chaque marqueur
        const group = new THREE.Group();
        group.position.copy(vector);

        // Sphère lumineuse (point)
        const sphereMat = new THREE.MeshBasicMaterial({
            color: item.color || 0x3b82f6,
            transparent: true,
            opacity: 0.9
        });
        const sphere = new THREE.Mesh(new THREE.SphereGeometry(0.05, 8, 8), sphereMat);
        group.add(sphere);

        // Cercle de halo
        const ringMat = new THREE.MeshBasicMaterial({
            color: item.color || 0x3b82f6,
            transparent: true,
            opacity: 0.3,
            side: THREE.DoubleSide
        });
        const ring = new THREE.Mesh(new THREE.RingGeometry(0.06, 0.10, 16), ringMat);
        ring.rotation.x = Math.PI / 2;
        group.add(ring);

        // Ligne verticale (comme une "tige")
        const lineMat = new THREE.LineBasicMaterial({ color: item.color || 0x3b82f6, transparent: true, opacity: 0.2 });
        const points = [
            new THREE.Vector3(0, 0, 0),
            new THREE.Vector3(0, -0.3, 0)
        ];
        const lineGeo = new THREE.BufferGeometry().setFromPoints(points);
        const line = new THREE.Line(lineGeo, lineMat);
        group.add(line);

        // Ajouter un petit label en sprite (pour afficher l'année)
        // Ici, on va créer un canvas pour le label
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.width = 64;
        canvas.height = 64;
        context.fillStyle = 'rgba(0,0,0,0)';
        context.fillRect(0, 0, canvas.width, canvas.height);
        context.font = 'bold 24px Arial';
        context.textAlign = 'center';
        context.textBaseline = 'middle';
        context.fillStyle = '#ffffff';
        context.fillText(item.year, 32, 32);

        const texture = new THREE.CanvasTexture(canvas);
        const spriteMat = new THREE.SpriteMaterial({ map: texture, transparent: true, depthTest: false });
        const sprite = new THREE.Sprite(spriteMat);
        sprite.scale.set(0.2, 0.2, 1);
        sprite.position.set(0, 0.4, 0);
        group.add(sprite);

        timelineScene.add(group);
        markersGroup.push(group);
    });
}

// Initialiser au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
    // Attendre un peu que le DOM soit prêt
    setTimeout(initTimeline, 300);
});