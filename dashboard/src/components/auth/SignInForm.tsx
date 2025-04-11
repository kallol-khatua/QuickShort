import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { EyeCloseIcon, EyeIcon } from "../../icons";
import Label from "../form/Label";
import Input from "../form/input/InputField";
import Button from "../ui/button/Button";
import axios from "axios";
import { useDispatch } from "react-redux";
import { login } from "../../redux/authSlice";
import { SuccessApiResponse } from "../../helper/SuccessApiResponse";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import toast from "react-hot-toast";

type ErrorField = {
  error: boolean;
  message?: string;
};
interface ErrorInterce {
  email: ErrorField;
  password: ErrorField;
}

interface DataInterface {
  email: string;
  password: string;
}

interface SigninResponseData extends SuccessApiResponse {
  data: { token: string };
}

export default function SignInForm() {
  const [showPassword, setShowPassword] = useState(false);
  const [data, setData] = useState<DataInterface>({
    email: "",
    password: "",
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<ErrorInterce>({
    email: {
      error: false,
    },
    password: {
      error: false,
    },
  });
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setData((prevData) => ({
      ...prevData,
      [e.target.name]: e.target.value,
    }));

    setErrors((prev) => ({
      ...prev,
      [e.target.name]: { error: false, message: "" },
    }));
  };

  // Function to handle login request
  const handleLogin = async () => {
    setLoading(true);

    try {
      const API_BASE_URL = import.meta.env.VITE_AUTHENTICATION_BACKEND_BASE_URL;
      const response = await axios.post<SigninResponseData>(
        `${API_BASE_URL}/signin`,
        data
      );

      // Dispatch token
      dispatch(login(response.data.data.token));

      // Redirect to / home route
      navigate("/", { replace: true });
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        const errorsInfo = errorData.errors;

        errorsInfo.map((info) => {
          // If error has field then set that to erroe
          if (info.field && info.field.length > 0) {
            setErrors((prev) => ({
              ...prev,
              [`${info.field}`]: { error: true, message: info.message },
            }));
          }
        });

        toast.error(errorData.message);
      } else {
        console.error("Unexpected error:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col flex-1">
      <div className="flex flex-col justify-center flex-1 w-full max-w-md mx-auto">
        <div>
          <div className="mb-5 sm:mb-8">
            <h1 className="mb-2 font-semibold text-gray-800 text-title-sm dark:text-white/90 sm:text-title-md">
              Sign In
            </h1>
            <p className="text-sm text-gray-500 dark:text-gray-400">
              Enter your email and password to sign in!
            </p>
          </div>

          <div>
            <div className="space-y-3">
              {/* Email */}
              <div>
                <Label>
                  Email <span className="text-error-500">*</span>{" "}
                </Label>
                <Input
                  placeholder="info@gmail.com"
                  name="email"
                  value={data.email}
                  onChange={(e) => handleChange(e)}
                  error={errors.email.error}
                />
                {errors.email.error && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.email.message}
                  </p>
                )}
              </div>

              {/* password */}
              <div>
                <Label>
                  Password <span className="text-error-500">*</span>{" "}
                </Label>
                <div className="relative">
                  <Input
                    type={showPassword ? "text" : "password"}
                    placeholder="Enter your password"
                    name="password"
                    value={data.password}
                    onChange={(e) => handleChange(e)}
                    error={errors.password.error}
                  />
                  <span
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute z-30 -translate-y-1/2 cursor-pointer right-4 top-1/2"
                  >
                    {showPassword ? (
                      <EyeIcon className="fill-gray-500 dark:fill-gray-400 size-5" />
                    ) : (
                      <EyeCloseIcon className="fill-gray-500 dark:fill-gray-400 size-5" />
                    )}
                  </span>
                </div>
                {errors.password.error && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.password.message}
                  </p>
                )}
              </div>

              {/* Forgot password */}
              <div className="flex items-center justify-end">
                <Link
                  to="/reset-password"
                  className="text-sm text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Forgot password?
                </Link>
              </div>

              {/* Sign in button */}
              <div>
                {loading ? (
                  <Button className="w-full" size="sm">
                    Submitting
                  </Button>
                ) : (
                  <Button className="w-full" size="sm" onClick={handleLogin}>
                    Sign in
                  </Button>
                )}
              </div>
            </div>

            <div className="mt-5">
              <p className="text-sm font-normal text-center text-gray-700 dark:text-gray-400 sm:text-start">
                Don&apos;t have an account? {""}
                <Link
                  to="/signup"
                  className="text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Sign Up
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
