async function resolveCurrentUser() {
  let session;
  try {
    const res = await fetch(`${CONFIG.GATEWAY_BASE}/api/session/me`, { credentials: "include" });
    if (!res.ok) throw new Error("not authenticated");
    
    const rawData = await res.json();
    // FIX: Safely extract data whether Spring nests it inside 'attributes' or not
    session = {
        name: rawData.name || rawData.attributes?.name,
        email: rawData.email || rawData.attributes?.email,
        picture: rawData.picture || rawData.attributes?.picture
    };
    
    sessionStorage.removeItem("fms_login_attempted");
  } catch (e) {
    if (sessionStorage.getItem("fms_login_attempted")) {
      document.body.innerHTML = `<h2>Couldn't sign you in</h2><p>Session not recognized.</p><a href="index.html">Back to start</a>`;
      return null;
    }
    sessionStorage.setItem("fms_login_attempted", "1");
    window.location.href = `${CONFIG.GATEWAY_BASE}/oauth2/authorization/google`;
    return null;
  }

  try {
      // Look the Google email up in user-service.
      const lookupRes = await fetch(
        `${CONFIG.GATEWAY_BASE}/api/users/by-email?email=${encodeURIComponent(session.email)}`,
        { credentials: "include" }
      );

      if (lookupRes.status === 200) {
        const user = await lookupRes.json();
        return { ...user, email: session.email, picture: session.picture };
      }

      // First time this Google account has ever signed in
      const throwawayPassword = crypto.randomUUID();
      const registerRes = await fetch(`${CONFIG.GATEWAY_BASE}/api/users/register`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: session.name, email: session.email, password: throwawayPassword }),
      });
      
      const user = await registerRes.json();
      return { ...user, email: session.email, picture: session.picture };
      
  } catch (err) {
      console.error("Profile sync failed:", err);
      // Return the Google session anyway so the UI doesn't freeze!
      return session; 
  }
}