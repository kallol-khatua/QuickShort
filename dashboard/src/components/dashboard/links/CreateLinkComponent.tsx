import React, { useState } from "react";
import { X } from "lucide-react";
import Label from "../../form/Label";
import Input from "../../form/input/InputField";
import axiosWorkspaceInstance from "../../../axiosWorkspaceInstance";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../../../redux/store";
import axios from "axios";
import { ErrorApiResponse } from "../../../helper/ErrorApiResponse";
import toast from "react-hot-toast";
import { logout } from "../../../redux/authSlice";
import { useNavigate } from "react-router-dom";

const CreateLinkComponent: React.FC<{
  handleLinkCreateModalToggle: () => void;
  handleLinkReload: () => void;
}> = ({ handleLinkCreateModalToggle, handleLinkReload }) => {
  const [url, setUrl] = useState("");
  const [errorData, setErrorData] = useState<string>("");
  const [error, setError] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleCreateUrl = async () => {
    if (!url) {
      setErrorData("Enter a valid URL");
      setError(true);
      return;
    }

    setLoading(true);

    try {
      const response = await axiosWorkspaceInstance.post(
        `/${currentWorkspace?.workspaceId.id}/shorten-url`,
        { originalUrl: url }
      );

      handleLinkCreateModalToggle();
      await navigator.clipboard.writeText(
        `${import.meta.env.VITE_URL_TRACKING_BASE_URL}/${
          response.data.data.shortCode
        }`
      );

      handleLinkReload();
      toast.success("Copied short link to clipboard!");
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        const errorData: ErrorApiResponse = err.response.data;
        toast.error(errorData.message);

        // if unauthorized or forbidden the logout using auth slice, protected route will take to signin page
        if (errorData.status_code === 401 || errorData.status_code === 403) {
          dispatch(logout());
        } else if (
          errorData.status_code === 429 &&
          currentWorkspace?.workspaceId.type === "FREE"
        ) {
          // if link creation limit exceeded and current
          navigate("upgrade");
        }
      } else {
        console.error("Unexpected error:", err);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void = (
    e
  ) => {
    setUrl(e.target.value);
    setErrorData("");
    setError(false);
  };

  return (
    <>
      {/* Modal Content */}
      <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg w-96">
        {/* Modal Header */}
        <div className="flex justify-between items-center border-b pb-3">
          <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
            New Link
          </h2>
          <button
            aria-label="n"
            onClick={handleLinkCreateModalToggle}
            className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div>
          <Label>
            Destination URL <span className="text-error-500">*</span>{" "}
          </Label>
          <div className="relative">
            <Input
              placeholder="https://example.com"
              name="url"
              type="url"
              value={url}
              onChange={(e) => {
                handleInputChange(e);
              }}
              error={error}
            />
          </div>
          {error && <p className="text-red-500 text-sm mt-1">{errorData}</p>}
        </div>

        {error && <p>{error}</p>}

        {/* Modal Footer */}
        <div className="mt-6 flex justify-end space-x-3">
          <button
            className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 transition"
            onClick={handleLinkCreateModalToggle}
          >
            Close
          </button>

          {loading ? (
            <button className="px-4 py-2 bg-blue-600 text-white rounded-md transition cursor-not-allowed">
              Submitting
            </button>
          ) : (
            <button
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition"
              onClick={handleCreateUrl}
            >
              Create
            </button>
          )}
        </div>
      </div>
    </>
  );
};

export default CreateLinkComponent;
