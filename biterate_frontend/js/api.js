/**
 * BiteRate — api.js
 * Serviço centralizado de chamadas à API REST
 */

const API_BASE = 'http://localhost:8080';

// ─── AUTH ───────────────────────────────────────────────────────────────────

function getToken()        { return localStorage.getItem('br_token'); }
function setToken(token)   { localStorage.setItem('br_token', token); }
function clearToken()      { localStorage.removeItem('br_token'); localStorage.removeItem('br_user'); }
function getUser()         { try { return JSON.parse(localStorage.getItem('br_user')); } catch { return null; } }
function setUser(user)     { localStorage.setItem('br_user', JSON.stringify(user)); }
function isLoggedIn()      { return !!getToken(); }

function requireAuth() {
  if (!isLoggedIn()) {
    const inPages = window.location.pathname.includes('/pages/');
    window.location.href = inPages ? 'login.html' : 'pages/login.html';
    return false;
  }
  return true;
}

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + getToken()
  };
}

async function apiRequest(method, path, body = null, auth = false) {
  const headers = auth ? authHeaders() : { 'Content-Type': 'application/json' };
  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(API_BASE + path, opts);
  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    const msg = data?.message || data?.error || 'Erro na requisição';
    throw new Error(msg);
  }
  return data;
}

// ─── AUTH ENDPOINTS ──────────────────────────────────────────────────────────

async function login(email, senha) {
  const data = await apiRequest('POST', '/api/auth/login', { email, senha });
  setToken(data.token);
  setUser(data.cliente);
  return data;
}

async function cadastrar(payload) {
  return apiRequest('POST', '/api/auth/cadastro', payload);
}

function logout() {
  clearToken();
  const inPages = window.location.pathname.includes('/pages/');
  window.location.href = inPages ? 'login.html' : 'pages/login.html';
}

// ─── RESTAURANTES ────────────────────────────────────────────────────────────

async function buscarRestaurantes(params = {}) {
  const qs = new URLSearchParams();
  if (params.termo)         qs.set('termo', params.termo);
  if (params.categorias?.length) params.categorias.forEach(c => qs.append('categorias', c));
  if (params.notaMinima)    qs.set('notaMinima', params.notaMinima);
  if (params.faixasPreco?.length) params.faixasPreco.forEach(f => qs.append('faixasPreco', f));
  if (params.comodidades?.length) params.comodidades.forEach(c => qs.append('comodidades', c));
  if (params.apenasAbertos) qs.set('apenasAbertos', 'true');
  if (params.ordenacao)     qs.set('ordenacao', params.ordenacao);
  if (params.page != null)  qs.set('page', params.page);
  if (params.size != null)  qs.set('size', params.size);
  return apiRequest('GET', '/api/restaurantes/buscar?' + qs.toString());
}

async function getRestaurante(id) {
  return apiRequest('GET', `/api/restaurantes/${id}`);
}

async function getAvaliacoesRestaurante(id, page = 0) {
  return apiRequest('GET', `/api/restaurantes/${id}/avaliacoes?page=${page}`);
}

async function favoritarRestaurante(id) {
  return apiRequest('POST', `/api/restaurantes/${id}/favoritar`, null, true);
}

async function isFavoritado(id) {
  return apiRequest('GET', `/api/restaurantes/${id}/favoritado`, null, true);
}

// ─── AVALIAÇÕES ──────────────────────────────────────────────────────────────

async function criarAvaliacao(payload) {
  return apiRequest('POST', '/api/avaliacoes', payload, true);
}

async function editarAvaliacao(id, payload) {
  return apiRequest('PUT', `/api/avaliacoes/${id}`, payload, true);
}

async function excluirAvaliacao(id) {
  return apiRequest('DELETE', `/api/avaliacoes/${id}`, null, true);
}

// ─── CLIENTE (PERFIL) ─────────────────────────────────────────────────────────

async function getPerfil() {
  return apiRequest('GET', '/api/clientes/perfil', null, true);
}

async function editarPerfil(payload) {
  return apiRequest('PUT', '/api/clientes/perfil', payload, true);
}

async function getMinhasAvaliacoes() {
  return apiRequest('GET', '/api/clientes/avaliacoes', null, true);
}

async function getMeusFavoritos() {
  return apiRequest('GET', '/api/clientes/favoritos', null, true);
}

// ─── HELPERS DE UI ───────────────────────────────────────────────────────────

function renderStars(nota) {
  let html = '';
  for (let i = 1; i <= 5; i++) {
    if (i <= Math.floor(nota)) html += '<i class="fa-solid fa-star star filled"></i>';
    else if (i - nota < 1)      html += '<i class="fa-solid fa-star-half-stroke star filled"></i>';
    else                         html += '<i class="fa-regular fa-star star"></i>';
  }
  return html;
}

function faixaPrecoLabel(fp) {
  const map = { '1': '💰', '2': '💰💰', '3': '💰💰💰', '4': '💰💰💰💰' };
  return map[fp] || fp || '';
}

function initNavbar() {
  const user = getUser();
  const loggedIn = !!user;

  // avatar: mostra inicial quando logado, redireciona para login quando não
  document.querySelectorAll('.nav-avatar').forEach(el => {
    el.style.cursor = 'pointer';
    if (loggedIn) {
      el.textContent = (user.nome || 'U')[0].toUpperCase();
      el.style.display = '';
      el.onclick = () => {
        const base = document.querySelector('base')?.href || '';
        const prefix = window.location.pathname.includes('/pages/') ? '' : 'pages/';
        window.location.href = prefix + 'perfil.html';
      };
    } else {
      el.style.display = 'none';
    }
  });

  // botão "Entrar" / "Cadastrar": aparece só quando deslogado
  document.querySelectorAll('.nav-auth-btn').forEach(el => {
    el.style.display = loggedIn ? 'none' : '';
  });

  // links exclusivos para logado (ex: "Meu perfil" no mobile menu)
  document.querySelectorAll('.nav-loggedin-btn').forEach(el => {
    el.style.display = loggedIn ? '' : 'none';
  });
}
