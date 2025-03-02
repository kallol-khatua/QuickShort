import { useDispatch, useSelector } from "react-redux";
import { RootState } from "../redux/store";
import { Navigate } from "react-router-dom";
import { setCurrentWorkspace } from "../redux/workspaceSlice";
import React, { useEffect } from "react";

const Home: React.FC = () => {
  const dispatch = useDispatch();
  const workspaces = useSelector(
    (state: RootState) => state.workspace.workspaces
  );
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  useEffect(() => {
    if (!currentWorkspace && workspaces.length > 0) {
      dispatch(setCurrentWorkspace(workspaces[0])); // Set first workspace as default
    }
  }, [currentWorkspace, workspaces, dispatch]);

  return (
    currentWorkspace !== null && (
      <Navigate to={`/${currentWorkspace.workspaceId.id}/`} />
    )
  );
};

export default Home;
