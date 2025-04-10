import React, { useState } from "react";
import SidebarProvider from "../context/SidebarProvider";
import { Outlet } from "react-router";
import { useSidebar } from "../hooks/useSidebar";
import AppHeader from "./AppHeader";
import AppSidebar from "./AppSidebar";
import ProtectedRoute from "./ProtectedRoute";
import WorkpaceLoader from "../components/WorkpaceLoader";
import CreateWorkspace from "./CreateWorkspace";

const LayoutContent: React.FC = () => {
  const { isExpanded, isMobileOpen } = useSidebar();

  // workspace dropdown and create worksapce modal
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isCreateWorkspaceModalOpen, setIsCreateWorkspaceModalOpen] =
    useState<boolean>(false);

  const handleWorksapceCreateModalToggle = () => {
    setIsCreateWorkspaceModalOpen((prev: boolean) => !prev);
    setIsOpen(false);
  };

  const handleWorkspaceDropdownToogle = () => {
    setIsOpen((prev: boolean) => !prev);
  };

  return (
    <div className="min-h-screen flex ">
      <div>
        <AppSidebar
          isOpen={isOpen}
          handleWorkspaceDropdownToogle={handleWorkspaceDropdownToogle}
          handleWorksapceCreateModalToggle={handleWorksapceCreateModalToggle}
        />

        {/* open create workspace model */}
        {isCreateWorkspaceModalOpen && (
          <CreateWorkspace
            handleWorksapceCreateModalToggle={handleWorksapceCreateModalToggle}
            isCreateWorkspaceModalOpen={isCreateWorkspaceModalOpen}
          />
        )}
      </div>

      <div
        className={`flex w-full flex-col min-h-screen transition-all duration-300 ease-in-out
      ${isExpanded ? "lg:ml-[290px]" : "lg:ml-[90px]"} 
      ${isMobileOpen ? "ml-0" : ""}`}
      >
        {/* App Header (Fixed height) */}
        <AppHeader />

        {/* Main Content (Takes remaining height) */}
        <div className="flex-1 flex flex-col p-4 mx-auto max-w-(--breakpoint-2xl) md:p-6 overflow-auto w-full h-min-0">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

const DashboardLayout: React.FC = () => {
  return (
    <ProtectedRoute>
      <WorkpaceLoader>
        <SidebarProvider>
          <LayoutContent />
        </SidebarProvider>
      </WorkpaceLoader>
    </ProtectedRoute>
  );
};

export default DashboardLayout;
