// import { Navigate } from "react-router-dom";
import axios from "axios";
import React, { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";

const ShortCode: React.FC = () => {
  const navigate = useNavigate();
  const { shortCode } = useParams();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(
          `${import.meta.env.VITE_ANALYTICS_BACKEND_BASE_URL}/${shortCode}`
        );
        // console.log(response);

        window.location.href = response.data.data.originalUrl;
      } catch (error) {
        console.error("Error fetching data:", error);
        navigate("/");
      }
    };

    fetchData();
  }, [navigate, shortCode]);

  return null;
};

export default ShortCode;
