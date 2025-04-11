import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { EyeCloseIcon, EyeIcon } from "../../icons";
import Label from "../form/Label";
import Input from "../form/input/InputField";
import Button from "../ui/button/Button";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import toast from "react-hot-toast";

type ErrorField = {
  error: boolean;
  message?: string;
};
interface ErrorInterce {
  email: ErrorField;
  password: ErrorField;
  firstName: ErrorField;
  lastName: ErrorField;
}

interface DataInterface {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export default function SignUpForm() {
  const [showPassword, setShowPassword] = useState(false);
  const [data, setData] = useState<DataInterface>({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<ErrorInterce>({
    email: {
      error: false,
    },
    password: {
      error: false,
    },
    firstName: {
      error: false,
    },
    lastName: {
      error: false,
    },
  });

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
  const handleSignup = async () => {
    let isAllDataProvided = true;
    if (data.email === "") {
      isAllDataProvided = false;
      setErrors((prev) => ({
        ...prev,
        email: { error: true, message: "Email is required" },
      }));
    }
    if (data.password === "") {
      isAllDataProvided = false;
      setErrors((prev) => ({
        ...prev,
        password: { error: true, message: "Password is required" },
      }));
    }
    if (data.lastName === "") {
      isAllDataProvided = false;
      setErrors((prev) => ({
        ...prev,
        lastName: { error: true, message: "Last name is required" },
      }));
    }
    if (data.firstName === "") {
      isAllDataProvided = false;
      setErrors((prev) => ({
        ...prev,
        firstName: { error: true, message: "First name is required" },
      }));
    }
    if (!isAllDataProvided) {
      return;
    }

    setLoading(true);

    try {
      const API_BASE_URL = import.meta.env.VITE_AUTHENTICATION_BACKEND_BASE_URL;
      await axios.post(`${API_BASE_URL}/register`, data);

      toast.success("Verify your email");

      // Redirect to /signin page
      navigate("/signin", { replace: true });
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
              Sign Up
            </h1>
          </div>

          <div>
            <div className="space-y-3">
              <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
                {/* <!-- First Name --> */}
                <div className="sm:col-span-1">
                  <Label>
                    First Name<span className="text-error-500">*</span>
                  </Label>
                  <Input
                    type="text"
                    id="firstName"
                    name="firstName"
                    placeholder="Enter your first name"
                    value={data.firstName}
                    onChange={(e) => handleChange(e)}
                    error={errors.firstName.error}
                  />
                  {errors.firstName.error && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.firstName.message}
                    </p>
                  )}
                </div>
                {/* <!-- Last Name --> */}
                <div className="sm:col-span-1">
                  <Label>
                    Last Name<span className="text-error-500">*</span>
                  </Label>
                  <Input
                    type="text"
                    id="lastName"
                    name="lastName"
                    placeholder="Enter your last name"
                    value={data.lastName}
                    onChange={(e) => handleChange(e)}
                    error={errors.lastName.error}
                  />
                  {errors.lastName.error && (
                    <p className="text-red-500 text-sm mt-1">
                      {errors.lastName.message}
                    </p>
                  )}
                </div>
              </div>

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

              {/* Sign up button */}
              <div className="mt-5">
                {loading ? (
                  <Button className="w-full" size="sm">
                    Submitting
                  </Button>
                ) : (
                  <Button className="w-full" size="sm" onClick={handleSignup}>
                    Sign up
                  </Button>
                )}
              </div>
            </div>

            <div className="mt-5">
              <p className="text-sm font-normal text-center text-gray-700 dark:text-gray-400 sm:text-start">
                Already have an account? {""}
                <Link
                  to="/signin"
                  className="text-brand-500 hover:text-brand-600 dark:text-brand-400"
                >
                  Sign In
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
