import React, { useState } from "react";
import { X } from "lucide-react";
import Label from "../../form/Label";
import Input from "../../form/input/InputField";
import axiosWorkspaceInstance from "../../../axiosWorkspaceInstance";
import { useDispatch } from "react-redux";
import axios from "axios";
import { ErrorApiResponse } from "../../../helper/ErrorApiResponse";
import toast from "react-hot-toast";
import { logout } from "../../../redux/authSlice";
import { useNavigate } from "react-router-dom";
import {
  setCurrentWorkspace,
  setIsLoaded,
} from "../../../redux/workspaceSlice";

const CreateWorkspaceComponent: React.FC<{
  handleWorksapceCreateModalToggle: () => void;
}> = ({ handleWorksapceCreateModalToggle }) => {
  const [name, setName] = useState("");
  const [errorData, setErrorData] = useState<string>("");
  const [error, setError] = useState<boolean>(false);
  const [loading, setLoading] = useState(false);

  const dispatch = useDispatch();
  const navigate = useNavigate();

  // crate workspace
  const handleCreateWorkspace = async () => {
    if (!name) {
      setErrorData("Enter a name");
      setError(true);
      return;
    }

    setLoading(true);

    try {
      const response = await axiosWorkspaceInstance.post("/", { name });
      toast.success("Workspace created");
      handleWorksapceCreateModalToggle();

      // reload all workspace
      dispatch(setIsLoaded(false));

      // set current to null so it will automatically match current workspace with recently creatd one
      dispatch(setCurrentWorkspace(null));

      // send to the created workspace
      navigate(`/${response.data.data.id}/`);
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

  const handleInputChange: (e: React.ChangeEvent<HTMLInputElement>) => void = (
    e
  ) => {
    setName(e.target.value);
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
            New Workspace
          </h2>
          <button
            aria-label="n"
            onClick={handleWorksapceCreateModalToggle}
            className="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div>
          <Label>
            Workspace name <span className="text-error-500">*</span>{" "}
          </Label>
          <div className="relative">
            <Input
              placeholder="My workspace"
              name="name"
              value={name}
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
            onClick={handleWorksapceCreateModalToggle}
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
              onClick={handleCreateWorkspace}
            >
              Create
            </button>
          )}
        </div>
      </div>
    </>
  );
};

export default CreateWorkspaceComponent;
