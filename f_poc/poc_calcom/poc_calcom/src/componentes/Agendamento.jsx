import Cal, { getCalApi } from "@calcom/embed-react";
import { useEffect } from "react";

export function Agendamento({ nomeUsuario, delay = "30min" }) {
  useEffect(() => {
    (async function () {
      const cal = await getCalApi();
      cal("ui", {
        theme: "light",
        styles: { branding: { brandColor: "#000000" } },
        hideEventTypeDetails: false,
        layout: "month_view",
      });
    })();
  }, []);

  if (!nomeUsuario) {
    return <p style={{ color: "red" }}>Nenhum nome de usuário do Cal.com informado.</p>;
  }

  return (
    <div style={{ width: "100%", height: "600px", border: "1px solid #ccc", borderRadius: "8px", overflow: "hidden" }}>
      <Cal
        calLink={`${nomeUsuario}/${delay}`}
        style={{ width: "100%", height: "100%", overflow: "scroll" }}
        config={{ layout: "month_view" }}
      />
    </div>
  );
}