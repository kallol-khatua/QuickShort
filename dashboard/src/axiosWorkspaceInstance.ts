import axios from 'axios';

const axiosWorkspaceInstance = axios.create({
    baseURL: import.meta.env.VITE_WORKSPACE_BACKEND_BASE_URL,
    withCredentials: true,
});

// Add a request interceptor to include the token in headers
axiosWorkspaceInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosWorkspaceInstance;
