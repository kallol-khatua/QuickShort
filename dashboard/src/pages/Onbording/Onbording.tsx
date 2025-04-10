import { useDispatch } from "react-redux";
import Label from "../../components/form/Label";
import Input from "../../components/form/input/InputField";
import { useNavigate } from "react-router-dom";
import axiosWorkspaceInstance from "../../axiosWorkspaceInstance";
import toast from "react-hot-toast";
import { setCurrentWorkspace, setIsLoaded } from "../../redux/workspaceSlice";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import { logout } from "../../redux/authSlice";
import { useState } from "react";

const Onbording: React.FC = () => {
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

      // reload all workspace
      dispatch(setIsLoaded(false));

      // set current to null so it will automatically match current workspace with recently creatd one
      dispatch(setCurrentWorkspace(null));

      // send to create link for newly created workspace
      navigate(`/onbording/link?workspace=${response.data.data.id}`);
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
    <div className=" min-h-screen flex flex-col justify-center items-center">
      <div>
        <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100 mb-5 text-center">
          Create a workspace
        </h2>

        <div className="max-w-sm min-w-sm">
          <Label>
            Workspace name <span className="text-error-500">*</span>{" "}
          </Label>
          <div className="relative mb-5">
            <Input
              placeholder="My workspace"
              name="name"
              value={name}
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
            <button
              className="px-4 py-2 bg-gray-700 dark:bg-white/[0.7] text-white dark:text-black rounded-md hover:bg-black dark:hover:bg-white transition w-full"
              onClick={handleCreateWorkspace}
            >
              Create workspace
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default Onbording;
