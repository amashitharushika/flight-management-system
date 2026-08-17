// Resolves "who is currently logged in" once per page load.
// Flow: ask the Gateway who the authenticated Google identity is,
// then look that email up in user-service, auto-creating a profile
// on first-ever login. Nothing is persisted client-side — this
// re-resolves each page load from the session cookie, which is the
// whole point of the BFF pattern (no token ever lives in the browser).

async function resolveCurrentUser() {
  let session;
  try {
    const res = await fetch(`${CONFIG.GATEWAY_BASE}/api/session/me`, { credentials: "include" });
    if (!res.ok) throw new Error("not authenticated");
    session = await res.json();
    sessionStorage.removeItem("fms_login_attempted");
  } catch (e) {
    // Loop guard: if we already tried redirecting to Google once this
    // session and came back still unauthenticated, STOP — show a
    // diagnostic message instead of silently bouncing forever.
    if (sessionStorage.getItem("fms_login_attempted")) {
      document.body.innerHTML = `
        <div style="padding:80px 20px;text-align:center;font-family:sans-serif;max-width:480px;margin:0 auto;">
          <h2>Couldn't sign you in</h2>
          <p style="color:#5B7A92;">Logged in with Google, but the session isn't being recognized on the next request. Check DevTools → Network → the /api/session/me request for the actual status code and any CORS error.</p>
          <a href="index.html" style="color:#2C4A63;font-weight:600;">Back to start</a>
        </div>`;
      return null;
    }
    sessionStorage.setItem("fms_login_attempted", "1");
    window.location.href = `${CONFIG.GATEWAY_BASE}/oauth2/authorization/google`;
    return null;
  }

  // Look the Google email up in user-service.
  const lookupRes = await fetch(
    `${CONFIG.GATEWAY_BASE}/api/users/by-email?email=${encodeURIComponent(session.email)}`,
    { credentials: "include" }
  );

  if (lookupRes.status === 200) {
    const user = await lookupRes.json();
    return { ...user, email: session.email, picture: session.picture };
  }

  // First time this Google account has ever signed in — auto-provision
  // a user-service profile. The password is a random, unusable throwaway;
  // this account only ever authenticates via Google going forward.
  const throwawayPassword = crypto.randomUUID();
  const registerRes = await fetch(`${CONFIG.GATEWAY_BASE}/api/users/register`, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name: session.name, email: session.email, password: throwawayPassword }),
  });
  const user = await registerRes.json();
  return { ...user, email: session.email, picture: session.picture };
}