import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../redux/store";
import axiosWorkspaceInstance from "../axiosWorkspaceInstance";
import axios from "axios";
import { ErrorApiResponse } from "../helper/ErrorApiResponse";
import { SuccessApiResponse } from "../helper/SuccessApiResponse";
import {
  setIsLoaded,
  setWorkspaces,
  WorkspaceMember,
} from "../redux/workspaceSlice";
import toast from "react-hot-toast";
import { logout } from "../redux/authSlice";
import { Navigate, useNavigate } from "react-router-dom";
import RotatingLoader from "./ui/loader/RotatingLoader";

interface WorkspaceResponseData extends SuccessApiResponse {
  data: WorkspaceMember[];
}

// Check workspace are empty or not
// if empty then redirect to onbording page
const ChildComponentProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const workspaces = useSelector(
    (state: RootState) => state.workspace.workspaces
  );

  return workspaces.length === 0 ? <Navigate to="/onbording" /> : children;
};

// Load workspaces if not loaded yet
const WorkpaceLoader: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const dispatch = useDispatch();
  const isLoaded = useSelector((state: RootState) => state.workspace.isLoaded);
  const naviagate = useNavigate();

  useEffect(() => {
    // if not isLoaded = fasle then load workspaces
    if (!isLoaded) {
      const fetchData = async () => {
        try {
          const response =
            await axiosWorkspaceInstance.get<WorkspaceResponseData>("/");

          dispatch(setWorkspaces(response.data.data));
          dispatch(setIsLoaded(true));
        } catch (err: unknown) {
          //   console.log(err);

          if (axios.isAxiosError(err) && err.response) {
            const errorData: ErrorApiResponse = err.response.data;
            toast.error(errorData.message);

            // if unauthorized the logout using auth slice, protected route will take to signin page
            if (errorData.status_code === 401) {
              dispatch(logout());
            } else {
              naviagate("/error");
            }
          } else {
            console.error("Unexpected error:", err);
            naviagate("/error");
          }
        }
      };

      fetchData();
    }
  }, [isLoaded, dispatch, naviagate]);

  // is not loaded then show loading spinner else show children nodes
  return isLoaded ? (
    <ChildComponentProvider>{children}</ChildComponentProvider>
  ) : (
    <div className="w-full h-full min-h-screen flex items-center justify center">
      <RotatingLoader />
    </div>
  );
};

export default WorkpaceLoader;
