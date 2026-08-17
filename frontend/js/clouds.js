function renderCloudField() {
  const field = document.createElement("div");
  field.className = "cloud-field";
  field.setAttribute("aria-hidden", "true");

  const cloudSvg = (w, h) => `
    <svg viewBox="0 0 200 100" xmlns="http://www.w3.org/2000/svg">
      <ellipse cx="60" cy="60" rx="50" ry="30" fill="#ffffff"/>
      <ellipse cx="100" cy="45" rx="42" ry="34" fill="#ffffff"/>
      <ellipse cx="140" cy="60" rx="46" ry="28" fill="#ffffff"/>
      <ellipse cx="100" cy="70" rx="70" ry="24" fill="#ffffff"/>
    </svg>`;

  const clouds = [
    { top: "6%",  size: 180, duration: 70, delay: -10, opacity: 0.5 },
    { top: "18%", size: 120, duration: 55, delay: -30, opacity: 0.4 },
    { top: "32%", size: 220, duration: 90, delay: -5,  opacity: 0.35 },
    { top: "50%", size: 140, duration: 65, delay: -45, opacity: 0.3 },
    { top: "68%", size: 200, duration: 80, delay: -20, opacity: 0.28 },
    { top: "82%", size: 110, duration: 50, delay: -15, opacity: 0.35 },
  ];

  clouds.forEach(c => {
    const el = document.createElement("div");
    el.className = "cloud";
    el.style.top = c.top;
    el.style.width = c.size + "px";
    el.style.height = (c.size * 0.5) + "px";
    el.style.opacity = c.opacity;
    el.style.animationDuration = c.duration + "s";
    el.style.animationDelay = c.delay + "s";
    el.innerHTML = cloudSvg(c.size, c.size * 0.5);
    field.appendChild(el);
  });

  document.body.prepend(field);
}

document.addEventListener("DOMContentLoaded", renderCloudField);