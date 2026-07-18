const CACHE_NAME = 'amc-v1';
const urlsToCache = [
  '/',
  '/css/style.css',
  '/js/chatbot.js',
  '/js/micro-interactions.js',
  '/js/sw-register.js',
  '/manifest.json',
  // Ajoutez ici d'autres ressources statiques (images, polices, etc.)
];

// Installation du service worker
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(urlsToCache))
      .then(() => self.skipWaiting())
  );
});

// Activation et nettoyage des anciens caches
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(cacheNames => {
      return Promise.all(
        cacheNames.map(name => {
          if (name !== CACHE_NAME) {
            return caches.delete(name);
          }
        })
      );
    })
  );
});

// Interception des requêtes : stratégie "Cache First" avec fallback réseau
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        if (response) {
          return response; // Retourne la réponse depuis le cache
        }
        return fetch(event.request).then(response => {
          // Mettre en cache les nouvelles ressources dynamiques (optionnel)
          if (!response || response.status !== 200 || response.type !== 'basic') {
            return response;
          }
          const responseToCache = response.clone();
          caches.open(CACHE_NAME)
            .then(cache => {
              cache.put(event.request, responseToCache);
            });
          return response;
        });
      })
  );
});