import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { Outlet, useNavigate, useParams } from "react-router-dom";
import RotatingLoader from "./ui/loader/RotatingLoader";
import { setCurrentWorkspace } from "../redux/workspaceSlice";

const CurrentWorkspacehandler: React.FC = () => {
  const navigate = useNavigate();
  const { workspaceId } = useParams();
  const workspaces = useSelector(
    (state: RootState) => state.workspace.workspaces
  );
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );
  const dispatch = useDispatch();

  useEffect(() => {
    if (!currentWorkspace && workspaceId) {
      // match with workspaceid with all the workspaces and set the updated one
      let current = null;

      for (const workspace of workspaces) {
        if (workspace.workspaceId.id === workspaceId) {
          current = workspace;
        }
      }

      // if no match found the navigate to / route
      if (!current) {
        navigate("/", { replace: true });
      } else {
        setTimeout(() => {
          dispatch(setCurrentWorkspace(current));
        }, 250);
      }
    }
  }, [currentWorkspace, dispatch, navigate, workspaceId, workspaces]);

  //   return <Outlet />;
  return currentWorkspace ? (
    <Outlet />
  ) : (
    <div className="w-full h-full min-h-screen flex items-center justify center">
      <RotatingLoader />
    </div>
  );
};

export default CurrentWorkspacehandler;
