import { createContext } from "react";

type SidebarContextType = {
  isExpanded: boolean;
  isMobileOpen: boolean;
  activeItem: string | null;
  openSubmenu: string | null;
  toggleSidebar: () => void;
  toggleMobileSidebar: () => void;
  setActiveItem: (item: string | null) => void;
  toggleSubmenu: (item: string) => void;
};

export const SidebarContext = createContext<SidebarContextType | undefined>(
  undefined
);
