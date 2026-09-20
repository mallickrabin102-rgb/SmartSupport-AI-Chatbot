const state = { faqs: [], sending: false };

const $ = (id) => document.getElementById(id);

document.addEventListener("DOMContentLoaded", () => {
  bindNavigation();
  bindChat();
  bindQuickQuestions();
  $("clearChat").addEventListener("click", clearChat);
  $("refreshBtn").addEventListener("click", () => {
    const active = document.querySelector(".section.active")?.id;
    if (active === "section-dashboard") loadDashboard();
    else if (active === "section-knowledge") loadFaqs();
    else showToast("Chat is ready.");
  });
  $("dashboardRefresh").addEventListener("click", loadDashboard);
  $("faqSearch").addEventListener("input", renderFaqs);
  loadBusiness();
  loadFaqs();
  loadDashboard();
});

function bindNavigation() {
  document.querySelectorAll(".nav-item").forEach(btn => {
    btn.addEventListener("click", () => {
      const section = btn.dataset.section;
      document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
      btn.classList.add("active");
      document.querySelectorAll(".section").forEach(x => x.classList.remove("active"));
      $("section-" + section).classList.add("active");
      $("pageTitle").textContent =
        section === "chat" ? "How can we help?" :
        section === "dashboard" ? "Support dashboard" : "Knowledge base";
      $("sidebar").classList.remove("open");
      if (section === "dashboard") loadDashboard();
      if (section === "knowledge") loadFaqs();
    });
  });
  $("mobileMenu").addEventListener("click", () => $("sidebar").classList.toggle("open"));
}

function bindChat() {
  $("chatForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const input = $("messageInput");
    const message = input.value.trim();
    if (!message || state.sending) return;
    input.value = "";
    addMessage(message, "user");
    await sendMessage(message);
  });
}

function bindQuickQuestions() {
  document.querySelectorAll("#quickQuestions button").forEach(btn => {
    btn.addEventListener("click", () => {
      const q = btn.dataset.question;
      $("messageInput").value = q;
      $("chatForm").requestSubmit();
    });
  });
}

async function sendMessage(message) {
  state.sending = true;
  setTyping(true);
  try {
    const response = await fetch("/api/chat", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({ message })
    });
    if (!response.ok) throw new Error("Chat request failed");
    const data = await response.json();
    setTimeout(() => addMessage(data.response || data.answer || "I couldn't find an answer for that.", "bot"), 350);
  } catch (error) {
    setTimeout(() => addMessage("Sorry, I couldn't connect to the support server. Please try again.", "bot"), 350);
    showToast("Server connection failed");
  } finally {
    setTimeout(() => { setTyping(false); state.sending = false; }, 350);
  }
}

function addMessage(text, type) {
  const wrap = document.createElement("div");
  wrap.className = "message-row " + (type === "user" ? "user-row" : "bot-row");
  const avatar = type === "bot" ? '<div class="bot-avatar small">S</div>' : "";
  wrap.innerHTML = `${avatar}<div class="message-group"><div class="message ${type === "user" ? "user-message" : "bot-message"}">${escapeHtml(text)}</div><span class="time">${formatTime(new Date())}</span></div>`;
  $("messages").appendChild(wrap);
  $("messages").scrollTop = $("messages").scrollHeight;
}

function clearChat() {
  $("messages").innerHTML = "";
  addMessage("Hi! 👋 I'm your SmartSupport assistant. How can I help you today?", "bot");
  showToast("Chat cleared");
}

function setTyping(show) { $("typing").classList.toggle("show", show); }

async function loadFaqs() {
  try {
    const res = await fetch("/api/faqs");
    if (!res.ok) throw new Error();
    state.faqs = await res.json();
    renderFaqs();
  } catch {
    $("faqList").innerHTML = '<div class="loading-card">Unable to load FAQs. Make sure the Spring Boot server is running.</div>';
  }
}

function renderFaqs() {
  const term = $("faqSearch").value.trim().toLowerCase();
  const list = state.faqs.filter(f =>
    !term || String(f.question || "").toLowerCase().includes(term) || String(f.answer || "").toLowerCase().includes(term)
  );
  if (!list.length) {
    $("faqList").innerHTML = '<div class="loading-card">No matching FAQs found.</div>';
    return;
  }
  $("faqList").innerHTML = list.map(f => `
    <article class="faq-item">
      <div class="faq-q"><span>${escapeHtml(f.question || "")}</span><span>+</span></div>
      <div class="faq-a">${escapeHtml(f.answer || "")}</div>
    </article>
  `).join("");
}

async function loadDashboard() {
  try {
    const res = await fetch("/api/dashboard");
    if (!res.ok) throw new Error();
    const d = await res.json();
    const total = Number(d.totalChats ?? d.total ?? 0);
    const answered = Number(d.answeredChats ?? d.answered ?? 0);
    const unanswered = Number(d.unansweredChats ?? d.unanswered ?? 0);
    const faqs = Number(d.totalFaqs ?? d.faqs ?? state.faqs.length ?? 0);
    $("statTotal").textContent = total;
    $("statAnswered").textContent = answered;
    $("statUnanswered").textContent = unanswered;
    $("statFaqs").textContent = faqs;
    const rate = total ? Math.round((answered / total) * 100) : 0;
    $("answerRate").textContent = rate + "%";
    document.querySelector(".health-ring").style.setProperty("--rate", rate + "%");
  } catch {
    // Keep UI usable even if the dashboard endpoint is unavailable.
  }
}

async function loadBusiness() {
  try {
    const res = await fetch("/api/business");
    if (!res.ok) return;
    const b = await res.json();
    if (b.name) {
      $("brandName").textContent = b.name;
      document.title = b.name + " — Customer Support";
    }
  } catch {}
}

function formatTime(date) {
  return date.toLocaleTimeString([], {hour: "2-digit", minute: "2-digit"});
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, ch => ({
    "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
  }[ch]));
}

let toastTimer;
function showToast(message) {
  const toast = $("toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("show"), 2200);
}
