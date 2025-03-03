import axios from "axios";

const axiosOrderInstance = axios.create({
  baseURL: import.meta.env.VITE_ORDER_BACKEND_BASE_URL,
});

// Add a request interceptor to include the token in headers
// axiosPaymentInstance.interceptors.request.use(
//   (config) => {
//     const token = localStorage.getItem("token");
//     if (token) {
//       config.headers["Authorization"] = `Bearer ${token}`;
//     }
//     return config;
//   },
//   (error) => {
//     return Promise.reject(error);
//   }
// );

export default axiosOrderInstance;
