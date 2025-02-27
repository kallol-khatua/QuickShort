import React from "react";
import SidebarProvider from "../context/SidebarProvider";
import { Outlet } from "react-router";
import { useSidebar } from "../hooks/useSidebar";
import AppHeader from "./AppHeader";
import AppSidebar from "./AppSidebar";
import ProtectedRoute from "./ProtectedRoute";

const LayoutContent: React.FC = () => {
  const { isExpanded, isMobileOpen } = useSidebar();

  return (
    <div className="min-h-screen md:flex">
      <div>
        <AppSidebar />
        {/* <Backdrop /> */}
      </div>
      <div
        className={`flex-1 transition-all duration-300 ease-in-out 
            ${isExpanded ? "lg:ml-[290px]" : "lg:ml-[90px]"} 
            ${isMobileOpen ? "ml-0" : ""} `}
      >
        <AppHeader />
        <div className="p-4 mx-auto max-w-(--breakpoint-2xl) md:p-6">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

const DashboardLayout: React.FC = () => {
  return (
    <ProtectedRoute>
      <SidebarProvider>
        <LayoutContent />
      </SidebarProvider>
    </ProtectedRoute>
  );
};

export default DashboardLayout;
