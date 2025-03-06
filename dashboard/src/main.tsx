import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { Provider } from "react-redux";
import { store } from "./redux/store.ts";
import { Toaster } from "react-hot-toast";
import { ThemeProvider } from "./context/ThemeProvider.tsx";

declare global {
  interface Window {
    Razorpay: any;
  }
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Provider store={store}>
      <ThemeProvider>
        <Toaster position="top-center" reverseOrder={false} />
        <App />
      </ThemeProvider>
    </Provider>
  </StrictMode>
);
