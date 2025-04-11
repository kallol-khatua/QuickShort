import { useDispatch } from "react-redux";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import { useNavigate, useSearchParams } from "react-router-dom";
import axiosWorkspaceInstance from "../../axiosWorkspaceInstance";
import toast from "react-hot-toast";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import { logout } from "../../redux/authSlice";
import { useState } from "react";

const AddLink: React.FC = () => {
  const [url, setUrl] = useState("");
  const [errorData, setErrorData] = useState<string>("");
  const [error, setError] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const workspace = searchParams.get("workspace");

  const handleGoToNextStep = () => {
    // navigate(`/`);
    navigate(`/${workspace}`);
  };

  const handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void = (
    e
  ) => {
    setUrl(e.target.value);
    setErrorData("");
    setError(false);
  };

  // crate workspace
  const handleCreateWorkspace = async () => {
    if (!url) {
      setErrorData("Enter a valid URL");
      setError(true);
      return;
    }

    setLoading(true);

    if (!workspace) return;

    try {
      await axiosWorkspaceInstance.post(`/${workspace}/shorten-url`, {
        originalUrl: url,
      });

      toast.success("Copied short link to clipboard!");

      // send to create link for newly created workspace
      handleGoToNextStep();
      //   navigate(`/${workspace}`);
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized or forbidden the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401 || errorData.status_code === 403) {
          dispatch(logout());
        }
      } else {
        console.error("Unexpected error:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className=" min-h-screen flex flex-col justify-center items-center">
      <div>
        <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-5 text-center">
          Create a link
        </h2>

        <div className="max-w-sm min-w-sm">
          <Label>
            Destination URL <span className="text-error-500">*</span>{" "}
          </Label>
          <div className="relative mb-5">
            <Input
              placeholder="https://example.com"
              name="url"
              value={url}
              onChange={(e) => {
                handleInputChange(e);
              }}
              error={error}
            />
            {error && <p className="text-red-500 text-sm mt-1">{errorData}</p>}
            {error && <p>{error}</p>}
          </div>

          {loading ? (
            <button className="px-4 py-2 bg-gray-700 dark:bg-white/[0.7] text-white dark:text-black rounded-md transition cursor-not-allowed w-full">
              Submitting
            </button>
          ) : (
            <div className="flex flex-col w-full">
              <button
                className="px-4 py-2 bg-gray-700 dark:bg-white/[0.7] text-white dark:text-black rounded-md hover:bg-black dark:hover:bg-white transition w-full"
                onClick={handleCreateWorkspace}
              >
                Create link
              </button>
              <button
                className="mt-2 text-gray-500 text-theme-sm dark:text-gray-400"
                onClick={handleGoToNextStep}
              >
                I'll do this later
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AddLink;