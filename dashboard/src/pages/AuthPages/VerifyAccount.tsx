import React from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import toast from "react-hot-toast";

const VerifyAccount: React.FC = () => {
  const [searchParams] = useSearchParams();
  const id = searchParams.get("id"); // assumes URL is like /verify?id=xxxx
  const navigate = useNavigate();

  const handleVerify = async () => {
    if (!id) {
      toast.error("Invalid id");
    }

    try {
      const API_BASE_URL = import.meta.env.VITE_AUTHENTICATION_BACKEND_BASE_URL;
      await axios.post(`${API_BASE_URL}/verify-account?id=${id}`);

      toast.success("Account verified successfully");
      navigate("/", { replace: true });
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;

        toast.error(errorData.message);
      } else {
        console.error("Unexpected error:", err);
      }
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100 px-4 dark:bg-gray-900">
      <div className="bg-white p-8 rounded-2xl shadow-lg max-w-md w-full text-center dark:bg-gray-800">
        <h1 className="text-2xl font-bold mb-4 text-gray-800 dark:text-white">
          Email Verification
        </h1>
        <p className="text-gray-600 mb-6 dark:text-white/[0.5]">
          Click the button below to verify your email and activate your account.
        </p>
        <button
          onClick={handleVerify}
          className="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-6 rounded-xl transition"
        >
          Verify Email
        </button>
      </div>
    </div>
  );
};

export default VerifyAccount;
