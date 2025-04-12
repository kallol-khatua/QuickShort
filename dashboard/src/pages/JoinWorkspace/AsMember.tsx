import toast from "react-hot-toast";
import axiosWorkspaceInstance from "../../axiosWorkspaceInstance";
import { useParams } from "react-router-dom";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import { useDispatch } from "react-redux";
import { logout } from "../../redux/authSlice";

const AsMember: React.FC = () => {
  const params = useParams();
  const dispatch = useDispatch();

  const handlJoinAsMember = async () => {
    try {
      await axiosWorkspaceInstance.post(
        `/${params.workspaceId}/join-as-member`
      );

      toast.success("Successfully applied to join workspace");
    } catch (err) {
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
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-white dark:bg-gray-900 transition-colors">
      <button
        onClick={handlJoinAsMember}
        className="px-6 py-2 rounded-2xl font-semibold shadow-md transition duration-300
      bg-blue-600 hover:bg-blue-700 text-white
      dark:bg-blue-500 dark:hover:bg-blue-600 dark:text-white"
      >
        Join Workspace
      </button>
    </div>
  );
};

export default AsMember;
