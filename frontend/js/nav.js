const PLANE_ICON = `<svg class="plane-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M21 12L3 5l3 7-3 7 18-7z" fill="#F5B93F"/>
</svg>`;

function renderNav(activePage) {
  const root = document.getElementById("nav-root");
  if (!root) return;

  const links = [
    { href: "search.html", label: "Search", key: "search" },
    { href: "my-bookings.html", label: "My Bookings", key: "bookings" },
    { href: "profile.html", label: "Profile", key: "profile" },
  ];

  root.innerHTML = `
    <nav class="nav">
      <a class="nav-brand" href="search.html">
        ${PLANE_ICON}
        <span>Skyward</span>
      </a>
      <div class="nav-links">
        ${links.map(l => `<a href="${l.href}" class="${l.key === activePage ? "active" : ""}">${l.label}</a>`).join("")}
      </div>
    </nav>
  `;
}