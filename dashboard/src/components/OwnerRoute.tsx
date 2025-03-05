import { useSelector } from "react-redux";
import { Navigate, Outlet } from "react-router-dom";
import { RootState } from "../redux/store";

function OwnerRoute() {
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  return currentWorkspace?.memberType === "OWNER" ? (
    <Outlet />
  ) : (
    <Navigate to={`/${currentWorkspace?.workspaceId.id}/`} />
  );
}

export default OwnerRoute;
