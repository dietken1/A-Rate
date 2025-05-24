import "./App.css";
import { ToastContainer } from "react-toastify";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Router from "./router";

function App() {
  const queryClient = new QueryClient();
  return (
    <QueryClientProvider client={queryClient}>
      <div className="App font-mono h-screen">
        <Router />
      </div>
      <ToastContainer
        aria-label="notification"
        progressClassName="progress-bar"
        position="bottom-right"
      />
    </QueryClientProvider>
  );
}

export default App;
